package com.erdouglass.common.messaging;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.PublisherDecorator;
import io.smallrye.reactive.messaging.rabbitmq.IncomingRabbitMQMetadata;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class LoggingDecorator implements PublisherDecorator {
  public static final String EVENT_TYPE = "X-Event-Type";
  
  private static final Logger LOGGER = Logger.getLogger(LoggingDecorator.class);
  private static final String CORRELATION_ID = "correlationId";

  @Override
  public Multi<? extends Message<?>> decorate(
      final Multi<? extends Message<?>> publisher, 
      final String channelName, 
      final boolean isConnector) {
    return publisher.invoke(message -> {
      message.getMetadata(IncomingRabbitMQMetadata.class).ifPresent(meta -> {
        var correlationId = meta.getCorrelationId().orElse(null);
        MDC.put(CORRELATION_ID, correlationId);
        var header = meta.getHeaders().get(EVENT_TYPE);
        var type = header != null ? header.toString() : message.getPayload().getClass().getSimpleName();
        LOGGER.infof("Received %s%s message on '%s' channel", type, message.getPayload(), channelName);
      });
      
      message.getMetadata(OutgoingRabbitMQMetadata.class).ifPresent(_ -> {
        LOGGER.infof("Sent %s message on '%s' channel", message.getPayload(), channelName);
      });
    });
  }
}
