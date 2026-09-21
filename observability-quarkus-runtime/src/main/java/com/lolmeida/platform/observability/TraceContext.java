package com.lolmeida.platform.observability;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import java.util.Optional;

/** Reads correlation identifiers from the current OpenTelemetry span without exporting it. */
public final class TraceContext {
  private TraceContext() {}

  public static Optional<String> traceId() {
    return currentSpanContext().map(SpanContext::getTraceId);
  }

  public static Optional<String> spanId() {
    return currentSpanContext().map(SpanContext::getSpanId);
  }

  private static Optional<SpanContext> currentSpanContext() {
    SpanContext context = Span.current().getSpanContext();
    if (!context.isValid()) return Optional.empty();
    return Optional.of(context);
  }
}
