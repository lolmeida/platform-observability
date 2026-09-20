package com.lolmeida.platform.observability;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "peah.observability")
public interface ObservabilityConfig {
  @WithDefault("true")
  boolean enabled();

  @WithDefault("unknown-service")
  String service();

  @WithDefault("local")
  String environment();

  @WithDefault("unknown")
  String version();

  Http http();

  RequestId requestId();

  interface Http {
    @WithDefault("1000")
    long slowThresholdMs();
  }

  interface RequestId {
    @WithDefault("X-Request-ID")
    String header();
  }
}
