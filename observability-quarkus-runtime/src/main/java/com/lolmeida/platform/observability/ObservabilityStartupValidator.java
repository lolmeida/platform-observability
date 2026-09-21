package com.lolmeida.platform.observability;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

@ApplicationScoped
public class ObservabilityStartupValidator {
  private final ObservabilityConfig config;

  public ObservabilityStartupValidator(ObservabilityConfig config) {
    this.config = config;
  }

  void validate(@Observes StartupEvent ignored) {
    if (config.enabled() && config.service().isBlank()) {
      throw new IllegalStateException("peah.observability.service is required when enabled");
    }
  }
}
