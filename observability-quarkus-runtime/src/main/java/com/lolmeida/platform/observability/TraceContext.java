package com.lolmeida.platform.observability;

import java.util.Optional;

/** Reads correlation identifiers from the current OpenTelemetry span without exporting it. */
public final class TraceContext {
  private TraceContext() {}

  public static Optional<String> traceId() {
    return value("getTraceId");
  }

  public static Optional<String> spanId() {
    return value("getSpanId");
  }

  private static Optional<String> value(String method) {
    try {
      Class<?> span = Class.forName("io.opentelemetry.api.trace.Span");
      Object current = span.getMethod("current").invoke(null);
      Object context = span.getMethod("getSpanContext").invoke(current);
      if (!(boolean) context.getClass().getMethod("isValid").invoke(context))
        return Optional.empty();
      return Optional.of((String) context.getClass().getMethod(method).invoke(context));
    } catch (ReflectiveOperationException | RuntimeException ignored) {
      return Optional.empty();
    }
  }
}
