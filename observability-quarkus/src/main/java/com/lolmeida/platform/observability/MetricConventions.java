package com.lolmeida.platform.observability;

import java.util.LinkedHashMap;
import java.util.Map;

/** Low-cardinality conventions for optional consumer-defined Micrometer metrics. */
public final class MetricConventions {
  private MetricConventions() {}

  public static String name(String component, String operation) {
    return normalize(component) + "." + normalize(operation);
  }

  public static Map<String, String> tags(String outcome, String category) {
    Map<String, String> tags = new LinkedHashMap<>();
    if (outcome != null && !outcome.isBlank()) tags.put("outcome", outcome);
    if (category != null && !category.isBlank()) tags.put("category", category);
    return Map.copyOf(tags);
  }

  private static String normalize(String value) {
    if (value == null || value.isBlank() || !value.matches("[a-zA-Z][a-zA-Z0-9_.-]{0,62}")) {
      throw new IllegalArgumentException("Metric component must be a bounded identifier");
    }
    return value.toLowerCase(java.util.Locale.ROOT).replace('-', '_');
  }
}
