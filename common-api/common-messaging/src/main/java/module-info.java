module com.erdouglass.common.messaging {
  requires jakarta.cdi;
  requires io.vertx.core;
  requires org.jboss.logging;
  requires io.smallrye.mutiny;
  requires smallrye.reactive.messaging.rabbitmq;
  requires smallrye.reactive.messaging.api;
  
  exports com.erdouglass.common.messaging;
}