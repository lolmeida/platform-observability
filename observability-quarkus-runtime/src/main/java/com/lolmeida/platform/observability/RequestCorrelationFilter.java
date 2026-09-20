package com.lolmeida.platform.observability;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

@Provider
@Priority(Priorities.AUTHENTICATION)
/** Reusable JAX-RS request correlation and HTTP outcome logger. */
public class RequestCorrelationFilter implements ContainerRequestFilter, ContainerResponseFilter {
  private static final Logger LOG = Logger.getLogger(RequestCorrelationFilter.class);
  private static final String REQUEST_ID = RequestCorrelationFilter.class.getName() + ".requestId";
  private static final String STARTED_AT = RequestCorrelationFilter.class.getName() + ".startedAt";
  private static final String PREVIOUS_MDC =
      RequestCorrelationFilter.class.getName() + ".previousMdc";
  private static final String VALID = "[A-Za-z0-9._:-]{1,128}";
  @Inject ObservabilityConfig config;

  @Override
  public void filter(ContainerRequestContext request) throws IOException {
    if (!config.enabled()) return;
    String id = request.getHeaderString(config.requestId().header());
    if (!isValidRequestId(id)) id = UUID.randomUUID().toString();
    request.setProperty(REQUEST_ID, id);
    request.setProperty(STARTED_AT, Instant.now());
    Map<String, Object> previous = new LinkedHashMap<>();
    previous.put("requestId", MDC.get("requestId"));
    previous.put("httpMethod", MDC.get("httpMethod"));
    previous.put("httpPath", MDC.get("httpPath"));
    request.setProperty(PREVIOUS_MDC, previous);
    MDC.put("requestId", id);
    MDC.put("httpMethod", request.getMethod());
    MDC.put("httpPath", normalizePath(request.getUriInfo().getPath()));
  }

  @Override
  public void filter(ContainerRequestContext request, ContainerResponseContext response)
      throws IOException {
    if (!config.enabled()) return;
    String id = (String) request.getProperty(REQUEST_ID);
    if (!isValidRequestId(id)) id = UUID.randomUUID().toString();
    Instant started = (Instant) request.getProperty(STARTED_AT);
    if (started == null) started = Instant.now();
    long duration = Math.max(0, Duration.between(started, Instant.now()).toMillis());
    int status = response.getStatus();
    response.getHeaders().putSingle(config.requestId().header(), id);
    Map<String, Object> fields = new LinkedHashMap<>();
    fields.put("requestId", id);
    fields.put("httpMethod", request.getMethod());
    fields.put("httpPath", normalizePath(request.getUriInfo().getPath()));
    fields.put("httpStatus", status);
    fields.put("durationMs", duration);
    fields.put("outcome", status >= 400 ? "failure" : "success");
    if (status >= 400 && !FailureLogger.wasLogged(request)) {
      fields.put("errorCategory", status == 401 || status == 403 ? "authorization" : "unexpected");
      StructuredLog.warn(LOG, "http.request.failed", "HTTP request failed", fields);
    } else if (status < 400)
      StructuredLog.info(LOG, "http.request.completed", "HTTP request completed", fields);
    if (duration >= config.http().slowThresholdMs())
      StructuredLog.info(LOG, "http.request.slow", "HTTP request exceeded slow threshold", fields);
    @SuppressWarnings("unchecked")
    Map<String, Object> previous = (Map<String, Object>) request.getProperty(PREVIOUS_MDC);
    restore("requestId", previous);
    restore("httpMethod", previous);
    restore("httpPath", previous);
  }

  private static void restore(String key, Map<String, Object> previous) {
    if (previous != null && previous.get(key) != null) MDC.put(key, previous.get(key));
    else MDC.remove(key);
  }

  public static boolean isValidRequestId(String value) {
    return value != null && value.matches(VALID);
  }

  public static String normalizePath(String path) {
    if (path == null || path.isBlank()) return "/";
    int query = path.indexOf('?');
    if (query >= 0) path = path.substring(0, query);
    String[] segments = path.split("/", -1);
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < segments.length; i++) {
      String segment = segments[i];
      if (segment.matches("[0-9a-fA-F]{8}-[0-9a-fA-F-]{27,36}")
          || segment.matches("\\d+")
          || (i > 0 && isDynamicAfter(segments[i - 1]))) segment = "{id}";
      result.append(segment);
      if (i < segments.length - 1) result.append('/');
    }
    return result.toString();
  }

  private static boolean isDynamicAfter(String parent) {
    return switch (parent) {
      case "realms",
              "clients",
              "roles",
              "users",
              "groups",
              "client-scopes",
              "mappers",
              "services" ->
          true;
      default -> false;
    };
  }
}
