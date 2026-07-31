package com.erdouglass.emdb.ingest.application.service;

import java.time.LocalDate;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import com.erdouglass.emdb.ingest.application.port.inbound.IngestMovieUseCase;
import com.erdouglass.emdb.ingest.application.port.outbound.IngestRepository;
import com.erdouglass.emdb.ingest.domain.event.IngestCompletedEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestExtractedEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestFailedEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestLoadedEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestStartedEvent;
import com.erdouglass.emdb.ingest.domain.exception.IngestNotFoundException;
import com.erdouglass.emdb.ingest.domain.model.IngestId;
import com.erdouglass.emdb.media.OriginalLanguage;
import com.erdouglass.emdb.media.ReleaseDate;
import com.erdouglass.emdb.media.SaveMovieCommand;
import com.erdouglass.emdb.media.SaveMovieUseCase;
import com.erdouglass.emdb.media.SourceId;
import com.erdouglass.emdb.media.SourceId.Source;
import com.erdouglass.emdb.media.Title;

@ApplicationScoped
class IngestMovieService implements IngestMovieUseCase {
  
  @Inject
  Event<IngestEvent> emitter;
  
  @Inject
  SaveMovieUseCase saveUseCase;
  
  @Inject
  IngestRepository repository;

  @Override
  public void ingest(IngestId id) {
    var ingest = repository.findById(id)
        .orElseThrow(() -> new IngestNotFoundException(id.toString()));   
    
    try {
      ingest.started();
      repository.save(ingest);
      emitter.fire(IngestStartedEvent.of(id, ingest.message()));      
      
      // Extract the media details from TMDB.
      var command = toSaveMovieCommand();
      ingest.extracted();
      repository.save(ingest);
      emitter.fire(IngestExtractedEvent.of(id, ingest.message()));
      
      // Load the movie details into the database.
      saveUseCase.save(command);
      ingest.loaded();
      repository.save(ingest);
      emitter.fire(IngestLoadedEvent.of(id, ingest.message()));
      
      ingest.completed();
      repository.save(ingest);
      emitter.fire(IngestCompletedEvent.of(id, ingest.message()));
    } catch (Exception e) {
      ingest.failed();
      repository.save(ingest);
      emitter.fire(IngestFailedEvent.of(id, ingest.message()));
      throw e;
    }
  }
  
  private SaveMovieCommand toSaveMovieCommand() {
    return SaveMovieCommand.builder()
        .sourceId(SourceId.of(Source.TMDB, "78"))
        .title(Title.of("Blade Runner"))
        .releaseDate(ReleaseDate.of(LocalDate.parse("1982-06-25")))
        .originalLanguage(OriginalLanguage.of("en"))
        .build();
  }
}
