package com.lolmeida.platform.observability;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/** Consumer configuration for generic observability behavior. */
@ApplicationScoped
public class ObservabilityConfig {
  @ConfigProperty(name = "peah.observability.enabled", defaultValue = "false")
  boolean enabled;

  @ConfigProperty(name = "peah.observability.service", defaultValue = "")
  String service;

  @ConfigProperty(name = "peah.observability.environment", defaultValue = "local")
  String environment;

  @ConfigProperty(name = "peah.observability.version", defaultValue = "unknown")
  String version;

  @ConfigProperty(name = "peah.observability.http.slow-threshold-ms", defaultValue = "1000")
  long slowThresholdMs;

  @ConfigProperty(name = "peah.observability.request-id.header", defaultValue = "X-Request-ID")
  String requestIdHeader;

  public boolean enabled() {
    return enabled;
  }

  public String service() {
    return service;
  }

  public String environment() {
    return environment;
  }

  public String version() {
    return version;
  }

  public Http http() {
    return new Http(slowThresholdMs);
  }

  public RequestId requestId() {
    return new RequestId(requestIdHeader);
  }

  public record Http(long slowThresholdMs) {}

  public record RequestId(String header) {}
}
