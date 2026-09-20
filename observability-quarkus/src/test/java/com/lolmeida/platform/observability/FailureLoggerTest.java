package com.lolmeida.platform.observability;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class FailureLoggerTest {
  @Test
  void markerIsStable() {
    assertNotNull(FailureLogger.FAILURE_LOGGED_PROPERTY);
  }
}
