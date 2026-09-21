package com.lolmeida.platform.observability;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MetricConventionsTest {
  @Test
  void normalizesMetricNamesAndKeepsTagsBounded() {
    assertEquals("cache.scope_fetch", MetricConventions.name("Cache", "scope-fetch"));
    assertEquals("success", MetricConventions.tags("success", "cache").get("outcome"));
  }

  @Test
  void rejectsUnboundedMetricComponents() {
    assertThrows(
        IllegalArgumentException.class, () -> MetricConventions.name("realm/secret", "fetch"));
  }
}
