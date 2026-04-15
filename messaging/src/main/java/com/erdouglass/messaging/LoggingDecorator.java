package com.erdouglass.messaging;

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
  public static final String CORRELATION_ID = "correlationId";
  private static final Logger LOGGER = Logger.getLogger(LoggingDecorator.class);
  
  @Override
  public Multi<? extends Message<?>> decorate(Multi<? extends Message<?>> publisher, 
                                              String channelName, 
                                              boolean isConnector) {
    return publisher.invoke(message -> {      
      message.getMetadata(IncomingRabbitMQMetadata.class).ifPresent(inMeta -> {
        var correlationId = inMeta.getCorrelationId().orElse("none");
        MDC.put(CORRELATION_ID, correlationId);
        var headerType = inMeta.getHeaders().get("X-Event-Type");
        String type = headerType != null ? headerType.toString() 
              : message.getPayload().getClass().getSimpleName();        
        LOGGER.infof("Received %s%s message on channel '%s'", type, message.getPayload(), channelName);
      });
      
      message.getMetadata(OutgoingRabbitMQMetadata.class).ifPresent(_ -> {
        LOGGER.infof("Sent %s message to channel '%s'", message.getPayload(), channelName);
      });      
    });    
  }
}