package com.erdouglass.emdb.ingest.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import com.erdouglass.emdb.ingest.application.port.in.IngestMediaCommand;
import com.erdouglass.emdb.ingest.application.port.in.SubmitIngestUseCase;
import com.erdouglass.emdb.ingest.application.port.out.IngestCommandPublisher;
import com.erdouglass.emdb.ingest.application.port.out.IngestRepository;
import com.erdouglass.emdb.ingest.domain.event.IngestEvent;
import com.erdouglass.emdb.ingest.domain.model.Ingest;
import com.erdouglass.emdb.ingest.domain.model.IngestId;
import com.erdouglass.emdb.ingest.messaging.IngestMediaMessage;

@ApplicationScoped
class SubmitIngestService implements SubmitIngestUseCase {
  
  @Inject
  Event<IngestEvent> events;
  
  @Inject
  IngestRepository jobs;
  
  @Inject
  IngestCommandPublisher commands;

  @Override
  public IngestId submit(IngestMediaCommand command) {
    var job = Ingest.submit(command.tmdbId(), command.mediaType());
    jobs.save(job);
    commands.publish(IngestMediaMessage.of(job.id().value(), job.tmdbId(), job.mediaType()));
    
    job.pullEvents().forEach(events::fire);
    return job.id();
  }
}
