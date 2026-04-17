package com.erdouglass.messaging;

import java.lang.reflect.Type;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Message;

import io.smallrye.reactive.messaging.MessageConverter;
import io.vertx.core.json.JsonObject;

@ApplicationScoped
public class JsonMessageConverter implements MessageConverter {
  
  @Override
  public boolean canConvert(Message<?> in, Type target) {
    return in.getPayload() instanceof JsonObject && target instanceof Class;
  }

  @Override
  public Message<?> convert(Message<?> in, Type target) {
    JsonObject json = (JsonObject) in.getPayload();
    Object converted = json.mapTo((Class<?>) target);
    return in.withPayload(converted);
  }
}
