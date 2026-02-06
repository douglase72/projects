package com.erdouglass.emdb.job.service;

import java.util.concurrent.CompletionStage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.job.mapper.IngestStatusChangedMapper;
import com.erdouglass.emdb.job.repository.IngestJobRepository;

import io.smallrye.reactive.messaging.annotations.Blocking;

/// Consumes {@link IngestStatusChanged} events to maintain a persistent history of 
/// ingest operations.
///
/// This consumer listens to the {@code ingest-status-in} channel and updates the
/// persistent store (Database) whenever an {@link IngestStatusChanged} event occurs.
/// It acts as the "Historian" of the system, ensuring that every state change
/// (e.g., SUBMITTED -> STARTED -> COMPLETED) is recorded reliably.
@ApplicationScoped
public class IngestJobService {
  private static final Logger LOGGER = Logger.getLogger(IngestJobService.class);
  
  @Inject
  IngestStatusChangedMapper mapper;
  
  @Inject
  IngestJobRepository repository;

  /// Persists the {@link IngestStatusChanged} event to the database.
  ///
  /// **Concurrency Strategy:**
  /// This method is annotated with {@link Blocking} to enforce sequential execution.
  /// This prevents race conditions (e.g., {@code OptimisticLockingFailureException})
  /// where parallel threads might attempt to INSERT and UPDATE the same Job ID simultaneously.
  ///
  /// @param wrapper The reactive message containing the event payload.
  /// @return A {@link CompletionStage} indicating the Ack/Nack status of the message.
  @Blocking
  @Transactional
  @Incoming("ingest-status-in")
  public CompletionStage<Void> onMessage(Message<IngestStatusChanged> wrapper) {
    var event = wrapper.getPayload();
    LOGGER.debugf("Received: %s", event);
    
    try {
      var job = mapper.toIngestJob(event);
      repository.save(job);
      LOGGER.infof("Saved: %s", job);
      return wrapper.ack();
    } catch (Exception e) {
      var msg = String.format("Failed to persist event %s", event);
      LOGGER.error(msg, e);
      return wrapper.nack(e);
    }
  }
  
}
