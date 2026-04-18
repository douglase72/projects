package com.erdouglass.emdb.media.messaging;

import java.util.concurrent.TimeUnit;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.common.MediaType;
import com.erdouglass.emdb.common.comand.IngestMedia;
import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.common.event.IngestStatusChanged.IngestSource;
import com.erdouglass.emdb.common.event.IngestStatusChanged.IngestStatus;
import com.erdouglass.emdb.media.annotation.IngestStatusContext;
import com.erdouglass.emdb.media.annotation.MessageMetadata;
import com.erdouglass.emdb.media.annotation.SendStatus;

@ApplicationScoped
public class MovieConsumer {
  
  @Inject 
  IngestStatusContext statusContext;
  
  @SendStatus
  public void ingest(Message<IngestMedia> message) {
    
    try {
      // Simulate extracting the movie data from TMDB.
      TimeUnit.SECONDS.sleep(2);
      statusContext.set(IngestStatusChanged.builder()          
        .id(MessageMetadata.getCorrelationId(message))
        .tmdbId(message.getPayload().tmdbId())
        .status(IngestStatus.COMPLETED)
        .source(IngestSource.MEDIA)
        .type(MediaType.MOVIE)
        .build());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
