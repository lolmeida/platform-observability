package com.lolmeida.platform.observability;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

/** Emits contract-compatible fields through the consumer's configured logger. */
public final class StructuredLog {
  private static final Set<String> DENY_LIST =
      Set.of("authorization", "cookie", "token", "secret", "password", "credential", "privatekey");
  private static final Set<String> COMMON =
      Set.of(
          "service", "environment", "version", "logger", "event", "message", "traceId", "spanId");

  private StructuredLog() {}

  public static void debug(Logger logger, String event, String message, Map<String, ?> fields) {
    write(logger, Logger.Level.DEBUG, event, message, fields, null);
  }

  public static void info(Logger logger, String event, String message, Map<String, ?> fields) {
    write(logger, Logger.Level.INFO, event, message, fields, null);
  }

  public static void warn(Logger logger, String event, String message, Map<String, ?> fields) {
    write(logger, Logger.Level.WARN, event, message, fields, null);
  }

  public static void error(
      Logger logger, String event, String message, Map<String, ?> fields, Throwable failure) {
    write(logger, Logger.Level.ERROR, event, message, fields, failure);
  }

  private static void write(
      Logger logger,
      Logger.Level level,
      String event,
      String message,
      Map<String, ?> fields,
      Throwable failure) {
    if (logger == null || event == null || message == null) return;
    Map<String, Object> previous = new HashMap<>();
    Set<String> created = new java.util.HashSet<>();
    put(previous, created, "service", value("service", "unknown-service"));
    put(previous, created, "environment", value("environment", "local"));
    put(previous, created, "version", value("version", "unknown"));
    put(previous, created, "event", event);
    put(previous, created, "message", message);
    put(previous, created, "logger", logger.getName());
    TraceContext.traceId().ifPresent(id -> put(previous, created, "traceId", id));
    TraceContext.spanId().ifPresent(id -> put(previous, created, "spanId", id));
    if (fields != null)
      fields.forEach(
          (key, fieldValue) -> {
            if (safe(key, fieldValue)) put(previous, created, key, fieldValue);
          });
    try {
      switch (level) {
        case DEBUG -> logger.debug(message);
        case INFO -> logger.info(message);
        case WARN -> logger.warn(message);
        case ERROR -> {
          if (failure == null) logger.error(message);
          else logger.error(message, failure);
        }
        default -> logger.info(message);
      }
    } finally {
      created.forEach(
          key -> {
            if (previous.get(key) == null) MDC.remove(key);
            else MDC.put(key, previous.get(key));
          });
    }
  }

  private static boolean safe(String key, Object value) {
    if (key == null
        || value == null
        || COMMON.contains(key)
        || DENY_LIST.stream().anyMatch(d -> key.toLowerCase(Locale.ROOT).contains(d))) return false;
    String normalized = key.toLowerCase(Locale.ROOT);
    return !normalized.contains("email") && !normalized.contains("username");
  }

  private static void put(
      Map<String, Object> previous, Set<String> created, String key, Object value) {
    if (!previous.containsKey(key)) previous.put(key, MDC.get(key));
    created.add(key);
    MDC.put(key, String.valueOf(value));
  }

  private static String value(String key, String fallback) {
    try {
      return ConfigProvider.getConfig()
          .getOptionalValue("peah.observability." + key, String.class)
          .orElse(fallback);
    } catch (RuntimeException ignored) {
      return fallback;
    }
  }
}
