package com.lolmeida.platform.observability;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import java.util.Optional;

@ConfigMapping(prefix = "peah.observability")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface ObservabilityRuntimeConfig {
  /** Enables request correlation and HTTP events. */
  @WithDefault("false")
  boolean enabled();

  /** Identifies the consuming service. */
  @WithDefault("")
  Optional<String> service();

  /** Identifies the deployment environment. */
  @WithDefault("local")
  String environment();

  /** Identifies the application version. */
  @WithDefault("unknown")
  String version();

  /** HTTP event configuration. */
  Http http();

  /** Request correlation configuration. */
  RequestId requestId();

  interface Http {
    /** Emits slow-request events above this threshold. */
    @WithDefault("1000")
    long slowThresholdMs();
  }

  interface RequestId {
    /** Names the request correlation header. */
    @WithDefault("X-Request-ID")
    String header();
  }
}
