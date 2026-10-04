package com.erdouglass.emdb.ingest.adapter.in.messaging;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.common.messaging.Correlation;
import com.erdouglass.emdb.ingest.application.port.in.SaveIngestEventUseCase;
import com.erdouglass.emdb.ingest.domain.event.IngestLoaded;
import com.erdouglass.emdb.ingest.domain.model.IngestId;
import com.erdouglass.emdb.media.messaging.MediaSavedMessage;

import io.smallrye.common.annotation.Blocking;

@ApplicationScoped
class MediaConsumer {
  
  @Inject
  SaveIngestEventUseCase saveUseCase;

  @Blocking
  @Incoming("media-saved")
  CompletionStage<Void> onMessage(Message<MediaSavedMessage> message) {
    var ingestId = Correlation.from(message).get("ingest-id").map(UUID::fromString).orElseThrow();
    var event = message.getPayload();
    var ingestEvent = IngestLoaded.of(IngestId.of(ingestId), event.tmdbId(), event.mediaType());
    saveUseCase.save(ingestEvent);
    return message.ack();
  }
}
