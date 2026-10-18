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
  private static final String RABBITMQ_METADATA = "io.smallrye.reactive.messaging.rabbitmq.";

  @Override
  public boolean canConvert(Message<?> in, Type target) {
    if (!(target instanceof Class<?> cls) || cls == Object.class || cls == JsonObject.class) {
      return false;
    }
    var payload = in.getPayload();
    return payload instanceof JsonObject
        || (payload instanceof Buffer || payload instanceof byte[]) && fromRabbitMq(in);
  }

  @Override
  public Message<?> convert(Message<?> in, Type target) {
    var json = switch (in.getPayload()) {
      case JsonObject object -> object;
      case Buffer buffer     -> buffer.toJsonObject();
      case byte[] bytes      -> Buffer.buffer(bytes).toJsonObject();
      default -> throw new IllegalStateException("unexpected payload " + in.getPayload().getClass());
    };
    return in.withPayload(json.mapTo((Class<?>) target));
  }

  private static boolean fromRabbitMq(Message<?> in) {
    for (Object meta : in.getMetadata()) {
      if (meta.getClass().getName().startsWith(RABBITMQ_METADATA)) {
        return true;
      }
    }
    return false;
  }
}
