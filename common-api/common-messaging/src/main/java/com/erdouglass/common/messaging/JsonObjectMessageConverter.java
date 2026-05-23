package com.erdouglass.common.messaging;

import java.lang.reflect.Type;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Message;

import io.smallrye.reactive.messaging.MessageConverter;
import io.vertx.core.json.JsonObject;

@Priority(100)
@ApplicationScoped
public class JsonObjectMessageConverter implements MessageConverter {

  @Override
  public boolean canConvert(Message<?> in, Type target) {
    return in.getPayload() instanceof JsonObject
        && target instanceof Class<?> cls
        && cls != JsonObject.class
        && cls != Object.class;
  }

  @Override
  public Message<?> convert(Message<?> in, Type target) {
    var json = (JsonObject) in.getPayload();
    var pojo = json.mapTo((Class<?>) target);
    return in.withPayload(pojo);
  }
}
