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
import io.quarkus.resteasy.reactive.spi.ContainerRequestFilterBuildItem;
import io.quarkus.resteasy.reactive.spi.ContainerResponseFilterBuildItem;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

public class ObservabilityProcessor {
  private static final String FEATURE = "platform-observability";

  @BuildStep
  FeatureBuildItem feature() {
    return new FeatureBuildItem(FEATURE);
  }

  @BuildStep
  ValidationBuildItem validateConfiguration() {
    Config config = ConfigProvider.getConfig();
    if (config.getOptionalValue("peah.observability.enabled", Boolean.class).orElse(false)
        && config
            .getOptionalValue("peah.observability.service", String.class)
            .orElse("")
            .isBlank()) {
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
