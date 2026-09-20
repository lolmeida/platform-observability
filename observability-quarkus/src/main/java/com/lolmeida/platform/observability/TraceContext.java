package com.lolmeida.platform.observability;

import io.opentelemetry.api.trace.Span;
import java.util.Optional;

/** Reads correlation identifiers from the current OpenTelemetry span without exporting it. */
public final class TraceContext {
  private TraceContext() {}

  public static Optional<String> traceId() {
    var context = Span.current().getSpanContext();
    return context.isValid() ? Optional.of(context.getTraceId()) : Optional.empty();
  }

  public static Optional<String> spanId() {
    var context = Span.current().getSpanContext();
    return context.isValid() ? Optional.of(context.getSpanId()) : Optional.empty();
  }
}
