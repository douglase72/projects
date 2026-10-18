package com.erdouglass.emdb.media.movie.adapter.in.messaging;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletionStage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.common.annotation.Blocking;
import io.smallrye.reactive.messaging.kafka.api.IncomingKafkaRecordMetadata;

@ApplicationScoped
class MovieDeadLetterConsumer {
  private static final Logger LOGGER = Logger.getLogger(MovieDeadLetterConsumer.class);
  
  @ConfigProperty(name = "emdb.movie.data")
  Path dir;
  
  @Inject
  ObjectMapper mapper;

  @Blocking
  @Incoming("movies-scraped-dlq")
  CompletionStage<Void> onMessage(Message<byte[]> message) {
    var meta = message.getMetadata(IncomingKafkaRecordMetadata.class).orElseThrow();
    var target = dir.resolve(fileName(meta));
    try {
      Files.createDirectories(dir);
      Files.write(target, pretty(message.getPayload()));
    } catch (IOException e) {
      throw new UncheckedIOException("could not write dead letter to " + target, e);
    }
    LOGGER.warnf("Dead letter written to %s", target);
    return message.ack();
  }
  
  private static String fileName(IncomingKafkaRecordMetadata<?, ?> meta) {
    var key = meta.getKey();
    return (key != null ? key.toString() : meta.getPartition() + "-" + meta.getOffset()) + ".json";
  }
  
  private byte[] pretty(byte[] payload) {
    try {
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(mapper.readTree(payload));
    } catch (IOException e) {
      return payload;
    }
  }  
}
