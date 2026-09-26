package com.erdouglass.common.messaging;

import java.lang.reflect.Type;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Message;

import io.smallrye.reactive.messaging.MessageConverter;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

@Priority(100)
@ApplicationScoped
public class JsonObjectMessageConverter implements MessageConverter {

  @Override
  public boolean canConvert(Message<?> in, Type target) {
    var payload = in.getPayload();
    return (payload instanceof JsonObject
            || payload instanceof Buffer
            || payload instanceof byte[])
        && target instanceof Class<?> cls
        && cls != JsonObject.class
        && cls != Object.class;
  }

  @Override
  public Message<?> convert(Message<?> in, Type target) {
    var json = toJsonObject(in.getPayload());
    return in.withPayload(json.mapTo((Class<?>) target));
  }

  private static JsonObject toJsonObject(Object payload) {
    return switch (payload) {
      case JsonObject json -> json;
      case Buffer buffer   -> buffer.toJsonObject();
      case byte[] bytes    -> Buffer.buffer(bytes).toJsonObject();
      default -> throw new IllegalStateException(
          "Unexpected payload type: " + payload.getClass().getName());
    };
  }
}
