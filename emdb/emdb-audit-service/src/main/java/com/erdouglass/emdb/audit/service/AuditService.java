package com.erdouglass.emdb.audit.service;

import java.time.Duration;
import java.time.Instant;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.command.AuditMessage;

import io.smallrye.common.annotation.RunOnVirtualThread;

@ApplicationScoped
public class AuditService {
  private static final Logger LOGGER = Logger.getLogger(AuditService.class);
  
  @RunOnVirtualThread
  @Incoming("audit-log-in")
  public void onMessage(AuditMessage message) {
    var latency = Duration.between(message.timestamp(), Instant.now()).toMillis();
    LOGGER.infof("Received: %s, latency: %d ms", message, latency);
  }
  
}
