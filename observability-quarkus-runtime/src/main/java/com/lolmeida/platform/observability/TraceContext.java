package com.lolmeida.platform.observability;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import java.util.Optional;

/** Reads correlation identifiers from the current OpenTelemetry span without exporting it. */
public final class TraceContext {
  private TraceContext() {}

  public static Optional<String> traceId() {
    SpanContext context = Span.current().getSpanContext();
    if (!context.isValid()) return Optional.empty();
    return Optional.of(context.getTraceId());
  }

  public static Optional<String> spanId() {
    SpanContext context = Span.current().getSpanContext();
    if (!context.isValid()) return Optional.empty();
    return Optional.of(context.getSpanId());
  }
}
