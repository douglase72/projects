package com.erdouglass.common.messaging;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.eclipse.microprofile.reactive.messaging.Message;

import io.smallrye.reactive.messaging.kafka.api.IncomingKafkaRecordMetadata;

/// Opaque correlation context carried in Kafka record headers under the `ctx-` prefix.
///
/// The service that starts a process puts its identifiers here. Any service that reacts to
/// a message copies the context onto whatever it emits, without inspecting it. The service
/// that started the process reads it back. Values are UTF-8 strings; keys are lower-case,
/// dash-separated, and stored without the prefix.
///
/// Immutable: `with` and `without` return new instances.
public record Correlation(Map<String, String> entries) {

  public static final String PREFIX = "ctx-";

  private static final Pattern KEY = Pattern.compile("[a-z0-9]+(?:-[a-z0-9]+)*");
  private static final Correlation EMPTY = new Correlation(Map.of());

  public Correlation {
    Objects.requireNonNull(entries, "entries is required");
    entries.forEach((key, value) -> {
      requireValidKey(key);
      Objects.requireNonNull(value, "value is required for key '" + key + "'");
    });
    entries = Map.copyOf(entries);
  }

  // ---------------------------------------------------------------- factories

  public static Correlation empty() {
    return EMPTY;
  }

  public static Correlation of(String key, String value) {
    return new Correlation(Map.of(key, value));
  }

  /// Reads the context from an incoming message. Empty if the message did not come from
  /// Kafka or carried no `ctx-*` headers.
  public static Correlation from(Message<?> message) {
    Objects.requireNonNull(message, "message is required");
    return message.getMetadata(IncomingKafkaRecordMetadata.class)
        .map(meta -> fromHeaders(meta.getHeaders()))
        .orElse(EMPTY);
  }

  /// Keeps only `ctx-*` headers with a non-null value. Kafka allows duplicate header names;
  /// the last one wins.
  public static Correlation fromHeaders(Headers headers) {
    if (headers == null) {
      return EMPTY;
    }
    var entries = new LinkedHashMap<String, String>();
    for (Header header : headers) {
      var name = header.key();
      if (name != null && name.startsWith(PREFIX) && header.value() != null) {
        entries.put(name.substring(PREFIX.length()), new String(header.value(), UTF_8));
      }
    }
    return entries.isEmpty() ? EMPTY : new Correlation(entries);
  }

  // ------------------------------------------------------------------ queries

  public Optional<String> get(String key) {
    return Optional.ofNullable(entries.get(key));
  }

  public boolean isEmpty() {
    return entries.isEmpty();
  }

  // --------------------------------------------------------------- derivation

  public Correlation with(String key, String value) {
    var copy = new LinkedHashMap<>(entries);
    copy.put(key, value);
    return new Correlation(copy);
  }

  public Correlation without(String key) {
    if (!entries.containsKey(key)) {
      return this;
    }
    var copy = new LinkedHashMap<>(entries);
    copy.remove(key);
    return copy.isEmpty() ? EMPTY : new Correlation(copy);
  }

  // ------------------------------------------------------------------- output

  /// New headers holding only this context, for a record that has no other headers.
  public Headers toHeaders() {
    return copyTo(new RecordHeaders());
  }

  /// Adds this context to existing headers, replacing any `ctx-*` header with the same key
  /// so a context is never duplicated when a message is re-emitted.
  public Headers copyTo(Headers target) {
    Objects.requireNonNull(target, "target is required");
    entries.forEach((key, value) -> {
      var name = PREFIX + key;
      target.remove(name);
      target.add(name, value.getBytes(UTF_8));
    });
    return target;
  }

  @Override
  public String toString() {
    return "Correlation" + entries;
  }

  private static void requireValidKey(String key) {
    Objects.requireNonNull(key, "key is required");
    if (!KEY.matcher(key).matches()) {
      throw new IllegalArgumentException("invalid correlation key '" + key
          + "': use lower-case letters, digits and single dashes");
    }
  }
}
