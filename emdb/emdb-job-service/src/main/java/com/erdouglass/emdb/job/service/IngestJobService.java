package com.erdouglass.emdb.job.service;

import java.util.List;
import java.util.concurrent.CompletionStage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.job.mapper.IngestJobMapper;
import com.erdouglass.emdb.job.query.IngestJobDto;
import com.erdouglass.emdb.job.repository.IngestJobRepository;

import io.smallrye.common.annotation.RunOnVirtualThread;

@ApplicationScoped
public class IngestJobService {
  private static final Logger LOGGER = Logger.getLogger(IngestJobService.class);
  
  @Inject
  IngestJobMapper mapper;
  
  @Inject
  IngestJobRepository repository;

  @RunOnVirtualThread
  @Transactional
  @Incoming("job-history-in")
  public CompletionStage<Void> onMessage(Message<IngestStatusChanged> wrapper) {
    var event = wrapper.getPayload();
    LOGGER.infof("Received: %s", event);
    
    try {
      repository.findById(event.id()).ifPresentOrElse(j -> {
        j.status(event.status(), event.timestamp(), event.source(), event.message());
        j.name(event.name());
        j.emdbId(event.emdbId());
        j.message(event.message());
        repository.update(j);
      }, 
      () -> repository.insert(mapper.toIngestJob(event)));
      return wrapper.ack();
    } catch (Exception e) {
      var msg = String.format("Failed to persist event %s", event);
      LOGGER.error(msg, e);
      return wrapper.nack(e);
    }
  }
  
  @Transactional
  public List<IngestJobDto> findAll() {
    return repository.findAll().map(mapper::toIngestJobDto).toList();
  }
  
}
