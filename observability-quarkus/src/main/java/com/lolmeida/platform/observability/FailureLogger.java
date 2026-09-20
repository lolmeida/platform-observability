package com.lolmeida.platform.observability;

import jakarta.ws.rs.container.ContainerRequestContext;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jboss.logging.Logger;

/** Logs one sanitized primary event for an application-owned exception mapper. */
public final class FailureLogger {
  public static final String FAILURE_LOGGED_PROPERTY = FailureLogger.class.getName() + ".logged";

  private FailureLogger() {}

  public static void log(
      ContainerRequestContext request,
      Logger logger,
      Throwable failure,
      int status,
      String category) {
    if (request != null) {
      if (wasLogged(request)) return;
      request.setProperty(FAILURE_LOGGED_PROPERTY, Boolean.TRUE);
    }
    Map<String, Object> fields = new LinkedHashMap<>();
    fields.put("httpStatus", status);
    fields.put("errorCategory", category);
    if (status >= 500)
      StructuredLog.error(logger, "http.request.failed", "HTTP request failed", fields, failure);
    else StructuredLog.warn(logger, "http.request.failed", "HTTP request failed", fields);
  }

  public static boolean wasLogged(ContainerRequestContext request) {
    return request != null && Boolean.TRUE.equals(request.getProperty(FAILURE_LOGGED_PROPERTY));
  }
}
