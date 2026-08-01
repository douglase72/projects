package com.erdouglass.emdb.ingest.adapter.inbound.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.ingest.application.port.outbound.IngestRepository;
import com.erdouglass.emdb.ingest.application.port.outbound.MediaCatalog;
import com.erdouglass.emdb.ingest.application.port.outbound.MediaSource;
import com.erdouglass.emdb.ingest.domain.event.IngestCompletedEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestExtractedEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestFailedEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestLoadedEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestStartedEvent;
import com.erdouglass.emdb.ingest.domain.exception.IngestNotFoundException;
import com.erdouglass.emdb.ingest.domain.model.IngestId;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class IngestConsumer {
  
  @Inject
  Event<IngestEvent> emitter;
  
  @Inject 
  MediaCatalog mediaCatalog;
  
  @Inject
  MediaSource mediaSource;
  
  @Inject
  IngestRepository repository;  

  @RunOnVirtualThread
  @Incoming("ingest-media-in")
  public Uni<Void> onMessage(Message<IngestId> message) {
    var ingestId = message.getPayload();
    var ingest = repository.findById(ingestId)
        .orElseThrow(() -> new IngestNotFoundException(ingestId.toString()));
    
    try {
      ingest.started();
      repository.save(ingest);
      emitter.fire(IngestStartedEvent.of(ingestId, ingest.message()));
      
      // Extract the media details from TMDB.
      var media = mediaSource.extract(ingest.source());
      ingest.extracted();
      repository.save(ingest);
      emitter.fire(IngestExtractedEvent.of(ingestId, ingest.message()));      
      
      // Load the movie details into the database.
      mediaCatalog.load(media);
      ingest.loaded();
      repository.save(ingest);
      emitter.fire(IngestLoadedEvent.of(ingestId, ingest.message()));      

      ingest.completed();
      repository.save(ingest);
      emitter.fire(IngestCompletedEvent.of(ingestId, ingest.message()));
      return Uni.createFrom().completionStage(message.ack());
    } catch (Exception e) {
      ingest.failed();
      repository.save(ingest);
      emitter.fire(IngestFailedEvent.of(ingestId, ingest.message()));
      return Uni.createFrom().completionStage(message.nack(e));
    }
  }
}
