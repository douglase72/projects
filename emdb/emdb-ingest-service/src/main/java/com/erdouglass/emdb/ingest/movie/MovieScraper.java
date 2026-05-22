package com.erdouglass.emdb.ingest.movie;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.common.movie.SaveMovie;
import com.erdouglass.emdb.ingest.Scraper;

/// Fetches movie details from TMDB and publishes a [SaveMovie] command to
/// the media service.
///
/// This is the ingest-side counterpart to
/// [com.erdouglass.emdb.media.movie.MovieConsumer]: this side reads from
/// TMDB and writes to the message broker; the other side reads from the
/// broker and writes to the database. Splitting the work this way keeps
/// the slow external HTTP call out of the database transaction.
@ApplicationScoped
class MovieScraper extends Scraper<SaveMovie> {
  private static final String CREDITS = "credits";
  
  @Inject
  @RestClient
  MovieClient client;
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  @Channel("save-movie-out") 
  Emitter<SaveMovie> emitter;
  
  @Override
  protected SaveMovie getCommand(int tmdbId) {
    var tmdbMovie = client.findById(tmdbId, CREDITS); 
    var command = mapper.toSaveMovie(tmdbMovie);
    return command;
  }
  
  @Override
  protected Emitter<SaveMovie> getEmitter() {
    return emitter;
  }
}
