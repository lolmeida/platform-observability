package com.lolmeida.platform.observability.deployment;

import com.lolmeida.platform.observability.FailureLogger;
import com.lolmeida.platform.observability.ObservabilityConfig;
import com.lolmeida.platform.observability.ObservabilityStartupValidator;
import com.lolmeida.platform.observability.RequestCorrelationFilter;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

public class ObservabilityProcessor {
  private static final String FEATURE = "platform-observability";

  @BuildStep
  FeatureBuildItem feature() {
    return new FeatureBuildItem(FEATURE);
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
