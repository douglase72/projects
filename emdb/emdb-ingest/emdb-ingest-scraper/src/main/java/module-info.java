module com.erdouglass.emdb.ingest.scraper {
  requires io.smallrye.common.annotation;
  requires io.smallrye.mutiny;
  requires jakarta.cdi;
  requires jakarta.ws.rs;
  requires microprofile.config.api;
  requires microprofile.rest.client.api;
  requires org.jboss.logging;
  requires org.mapstruct;
  requires smallrye.reactive.messaging.api;
  requires smallrye.reactive.messaging.rabbitmq;
  
  requires transitive com.erdouglass.common.messaging;
  requires transitive com.erdouglass.emdb.ingest;
  requires transitive com.erdouglass.emdb.media;
  
  exports com.erdouglass.emdb.ingest.scraper;
}