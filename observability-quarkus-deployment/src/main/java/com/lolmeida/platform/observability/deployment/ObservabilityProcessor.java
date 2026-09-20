package com.lolmeida.platform.observability.deployment;

import com.lolmeida.platform.observability.FailureLogger;
import com.lolmeida.platform.observability.ObservabilityConfig;
import com.lolmeida.platform.observability.ObservabilityStartupValidator;
import com.lolmeida.platform.observability.RequestCorrelationFilter;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.builder.item.SimpleBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.ApplicationArchivesBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class ObservabilityProcessor {
  private static final String FEATURE = "platform-observability";

  @BuildStep
  FeatureBuildItem feature() {
    return new FeatureBuildItem(FEATURE);
  }

  @BuildStep
  ValidationBuildItem validateConfiguration(ApplicationArchivesBuildItem archives) {
    Path applicationProperties = archives.getRootArchive().getChildPath("application.properties");
    Properties properties = loadApplicationProperties(applicationProperties);
    if (Boolean.parseBoolean(properties.getProperty("peah.observability.enabled", "false"))
        && properties.getProperty("peah.observability.service", "").isBlank()) {
      throw new IllegalStateException(
          "peah.observability.service must be configured when observability is enabled");
    }
    return new ValidationBuildItem();
  }

  static final class ValidationBuildItem extends SimpleBuildItem {}

  private static Properties loadApplicationProperties(Path path) {
    Properties properties = new Properties();
    if (path == null || !Files.isRegularFile(path)) return properties;
    try (InputStream input = Files.newInputStream(path)) {
      properties.load(input);
      return properties;
    } catch (IOException exception) {
      throw new IllegalStateException(
          "Unable to read application.properties for observability validation", exception);
    }
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
