package com.erdouglass.emdb.ingest.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import com.erdouglass.emdb.ingest.application.port.in.IngestMediaUseCase;
import com.erdouglass.emdb.ingest.application.port.out.IngestJobRepository;
import com.erdouglass.emdb.ingest.application.port.out.MediaSource;
import com.erdouglass.emdb.ingest.application.port.out.Movie;
import com.erdouglass.emdb.ingest.application.port.out.MovieRepository;
import com.erdouglass.emdb.ingest.application.port.out.Person;
import com.erdouglass.emdb.ingest.application.port.out.PersonRepository;
import com.erdouglass.emdb.ingest.domain.event.IngestEvent;
import com.erdouglass.emdb.ingest.domain.exception.IngestJobNotFoundException;
import com.erdouglass.emdb.ingest.domain.model.IngestId;
import com.erdouglass.emdb.ingest.domain.model.IngestJob;

@ApplicationScoped
class IngestService implements IngestMediaUseCase {
  
  @Inject
  Event<IngestEvent> emitter;
  
  @Inject
  IngestJobRepository jobs;
  
  @Inject
  MediaSource source;
  
  @Inject
  MovieRepository movies;
  
  @Inject
  PersonRepository people;

  @Override
  public void ingest(IngestId id) {
    IngestJob job = jobs.findById(id)
        .orElseThrow(() -> new IngestJobNotFoundException(id));
    
    try {
      job.start();
      jobs.save(job);
      job.pullEvents().forEach(emitter::fire);
      
      // Extract the media from TMDB.
      var media = source.extract(job.tmdbId(), job.type());
      job.markExtracted();
      jobs.save(job);
      job.pullEvents().forEach(emitter::fire);
      
      // Send the media to the broker.
      switch (media) {
        case Movie m -> movies.save(m);
        case Person p -> people.save(p);
      }
    } catch (Exception e) {
      job.fail(e.getMessage());
      jobs.save(job);
      job.pullEvents().forEach(emitter::fire);   
      throw e;
    }
  }
}
