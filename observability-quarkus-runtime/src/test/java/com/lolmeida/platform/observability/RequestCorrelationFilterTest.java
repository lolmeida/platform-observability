package com.lolmeida.platform.observability;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RequestCorrelationFilterTest {
  @Test
  void validRequestIdIsAccepted() {
    assertTrue(RequestCorrelationFilter.isValidRequestId("req_01:abc-xyz"));
  }

  @Test
  void invalidRequestIdIsRejected() {
    assertFalse(RequestCorrelationFilter.isValidRequestId("bad id"));
    assertFalse(RequestCorrelationFilter.isValidRequestId("x".repeat(129)));
  }

  @Test
  void dynamicPathIsNormalizedWithoutQuery() {
    assertEquals(
        "/api/v1/services/{id}/clients/{id}",
        RequestCorrelationFilter.normalizePath("/api/v1/services/123/clients/abc?token=hidden"));
  }

  @Test
  void emptyPathIsRoot() {
    assertEquals("/", RequestCorrelationFilter.normalizePath(""));
  }
}
