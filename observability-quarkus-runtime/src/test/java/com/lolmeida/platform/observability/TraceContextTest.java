package com.lolmeida.platform.observability;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;
import io.opentelemetry.context.Scope;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class TraceContextTest {

  @Test
  void returnsEmptyWhenCurrentSpanIsInvalid() {
    assertEquals(Optional.empty(), TraceContext.traceId());
    assertEquals(Optional.empty(), TraceContext.spanId());
  }

  @Test
  void returnsTraceAndSpanFromCurrentSpan() {
    String traceId = "1234567890abcdef1234567890abcdef";
    String spanId = "1234567890abcdef";
    SpanContext context =
        SpanContext.create(traceId, spanId, TraceFlags.getDefault(), TraceState.getDefault());

    try (Scope ignored = Span.wrap(context).makeCurrent()) {
      assertEquals(Optional.of(traceId), TraceContext.traceId());
      assertEquals(Optional.of(spanId), TraceContext.spanId());
    }
  }
}
