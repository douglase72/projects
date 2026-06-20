package com.erdouglass.emdb.ingest.core.movie;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.ingest.IngestMedia;
import com.erdouglass.emdb.ingest.core.Log;
import com.erdouglass.emdb.media.command.MovieCommandService;
import com.erdouglass.emdb.media.query.MovieDto;

@ApplicationScoped
public class MovieIngestConsumer {
  
  @Inject
  TmdbMovieScraper scraper;
  
  @Inject
  MovieCommandService service;
  
  @Log
  public MovieDto ingest(Message<IngestMedia> message) {
    var payload = message.getPayload();
    var command = scraper.scrape(payload.tmdbId());
    var movie = service.save(command);
    return movie;
  }
}
