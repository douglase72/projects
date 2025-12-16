package com.erdouglass.emdb.audit.service;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.message.AuditMessage;

import io.smallrye.common.annotation.RunOnVirtualThread;

@ApplicationScoped
public class AuditService {
  private static final Logger LOGGER = Logger.getLogger(AuditService.class);
  
  @RunOnVirtualThread
  @Incoming("audit-log-in")
  public void onMessage(AuditMessage message) {
    LOGGER.infof("[%d%%] %s, %s, %s", 
        message.progress(), message.type(), message.source(), message.message());
  }
  
}
