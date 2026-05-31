module com.erdouglass.emdb.media.domain {
  requires com.fasterxml.uuid; 
  requires jakarta.cdi;
  requires jakarta.data;
  requires jakarta.inject;
  requires jakarta.persistence;
  requires jakarta.transaction;
  requires jakarta.validation;
  requires microprofile.config.api;
  requires org.hibernate.orm.core;
  requires org.jboss.logging;
  requires org.mapstruct;
  
  requires transitive com.erdouglass.emdb.media;

  exports com.erdouglass.emdb.media.domain;
}