package com.erdouglass.emdb.audit.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

import com.erdouglass.emdb.audit.entity.AuditLog;
import com.erdouglass.emdb.audit.repository.AuditRepository;
import com.erdouglass.emdb.common.command.AuditMessage;

import io.smallrye.common.annotation.RunOnVirtualThread;

@ApplicationScoped
public class AuditService {
  private static final Logger LOGGER = Logger.getLogger(AuditService.class);
  
  @Inject
  AuditRepository repository;
  
  @Transactional
  @RunOnVirtualThread
  @Incoming("audit-trail-in")
  public void onMessage(AuditMessage message) {
    var meta = message.meta();
    MDC.put("event.created", meta.timestamp().toString()); 
    
    try {
      var log = new AuditLog(meta.traceId());
      log.timestamp(meta.timestamp());
      log.tmdbId(message.tmdbId());
      log.source(meta.source());
      log.type(meta.type());
      log.message(meta.message());
      log.percentComplete(meta.percentComplete());
      var msg = String.format("[%d%%] Source: %s, Type: %s, Message: %s", 
          log.percentComplete(), log.source(), log.type(), log.message());
      repository.insert(log);
      LOGGER.info(msg);
    } finally {
      MDC.remove("event.created");
    }
  }
  
}
