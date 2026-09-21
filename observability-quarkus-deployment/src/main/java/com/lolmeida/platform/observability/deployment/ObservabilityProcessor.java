package com.lolmeida.platform.observability.deployment;

import com.lolmeida.platform.observability.FailureLogger;
import com.lolmeida.platform.observability.ObservabilityConfig;
import com.lolmeida.platform.observability.ObservabilityStartupValidator;
import com.lolmeida.platform.observability.RequestCorrelationFilter;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.builder.item.SimpleBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.ConfigurationBuildItem;
import io.quarkus.resteasy.reactive.spi.ContainerRequestFilterBuildItem;
import io.quarkus.resteasy.reactive.spi.ContainerResponseFilterBuildItem;
import io.smallrye.config.ConfigValue;

public class ObservabilityProcessor {
  private static final String FEATURE = "platform-observability";

  @BuildStep
  FeatureBuildItem feature() {
    return new FeatureBuildItem(FEATURE);
  }

  @BuildStep
  ValidationBuildItem validateConfiguration(ConfigurationBuildItem configuration) {
    var values = configuration.getReadResult().getRunTimeValues();
    ConfigValue enabled = values.get("peah.observability.enabled");
    ConfigValue service = values.get("peah.observability.service");
    if (enabled != null
        && Boolean.parseBoolean(enabled.getValue())
        && (service == null || service.getValue() == null || service.getValue().isBlank())) {
      throw new IllegalStateException(
          "peah.observability.service must be configured when observability is enabled");
    }
    return new ValidationBuildItem();
  }

  static final class ValidationBuildItem extends SimpleBuildItem {}

  @BuildStep
  void registerRequestFilter(BuildProducer<ContainerRequestFilterBuildItem> filters) {
    filters.produce(
        new ContainerRequestFilterBuildItem.Builder(RequestCorrelationFilter.class.getName())
            .build());
  }

  @BuildStep
  void registerResponseFilter(BuildProducer<ContainerResponseFilterBuildItem> filters) {
    filters.produce(
        new ContainerResponseFilterBuildItem.Builder(RequestCorrelationFilter.class.getName())
            .build());
  }

  @BuildStep
  void registerBeans(BuildProducer<AdditionalBeanBuildItem> beans) {
    beans.produce(
        AdditionalBeanBuildItem.builder()
            .addBeanClasses(
                ObservabilityConfig.class.getName(),
                ObservabilityStartupValidator.class.getName(),
                RequestCorrelationFilter.class.getName(),
                FailureLogger.class.getName())
            .setUnremovable()
            .build());
  }
}
