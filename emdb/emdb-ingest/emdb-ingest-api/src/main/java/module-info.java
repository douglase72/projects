module com.erdouglass.emdb.ingest {
  requires jakarta.cdi;
  requires jakarta.validation;
  requires com.fasterxml.jackson.annotation;
  requires smallrye.reactive.messaging.api;
  requires smallrye.reactive.messaging.rabbitmq;
  
  requires transitive com.erdouglass.common.messaging;
  
  exports com.erdouglass.emdb.ingest;
}