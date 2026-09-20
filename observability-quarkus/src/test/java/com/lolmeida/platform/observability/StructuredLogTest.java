package com.lolmeida.platform.observability;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.Test;

class StructuredLogTest {
  @Test
  void removesOnlyFieldsItTemporarilyOwns() {
    MDC.put("requestId", "req-1");
    StructuredLog.info(
        Logger.getLogger(getClass()),
        "test.event",
        "test",
        Map.of("token", "never-log", "safe", "value"));
    assertEquals("req-1", MDC.get("requestId"));
    assertNull(MDC.get("safe"));
    MDC.remove("requestId");
  }
}
