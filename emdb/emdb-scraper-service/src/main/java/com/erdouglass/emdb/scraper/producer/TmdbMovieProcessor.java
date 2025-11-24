package com.erdouglass.emdb.scraper.producer;

import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.command.MovieCreateCommand;
import com.erdouglass.emdb.common.query.MovieStatus;
import com.erdouglass.emdb.common.query.MovieStatus.MessageStatus;
import com.erdouglass.emdb.common.query.MovieStatus.MessageType;
import com.erdouglass.emdb.scraper.anno.LogValidation;
import com.erdouglass.emdb.scraper.client.TmdbMovieClient;
import com.erdouglass.emdb.scraper.dto.TmdbMovie;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.vertx.core.json.JsonObject;

@ApplicationScoped
public class TmdbMovieProcessor {
  private static final Logger LOGGER = Logger.getLogger(TmdbMovieProcessor.class);
  private static final String CREDITS = "credits";
  
  @Inject
  @Channel("movie-create-out") 
  Emitter<MovieCreateCommand> createEmitter;
  
  @Inject
  @Channel("movie-status-out") 
  Emitter<MovieStatus> statusEmitter;
  
  @Inject
  @RestClient
  TmdbMovieClient client;
  
  @Inject
  Validator validator;
  
  @LogValidation
  @RunOnVirtualThread
  @Incoming("movie-request-in")
  public void scrape(JsonObject jsonObject) {
    MovieStatus message = jsonObject.mapTo(MovieStatus.class);
    switch (message.type()) {
      case INGEST -> ingest(message.tmdbId());
      case SYNCHRONIZE -> { }
    };
  }
  
  private MovieCreateCommand createMessage(TmdbMovie movie) {
    return MovieCreateCommand.builder()
        .tmdbId(movie.id())
        .title(movie.title())
        .releaseDate(movie.release_date())
        .score(movie.vote_average())
        .status(movie.status())
        .runtime(movie.runtime())
        .budget(movie.budget())
        .revenue(movie.revenue())
        .homepage(movie.homepage())
        .originalLanguage(movie.original_language())
        .backdrop(movie.backdrop_path())
        .poster(movie.poster_path())
        .tagline(movie.tagline())
        .overview(movie.overview())
        .build();    
  }
  
  private TmdbMovie findById(int tmdbId) {
    var tmdbMovie = client.findById(tmdbId, CREDITS);
    Set<ConstraintViolation<TmdbMovie>> violations = validator.validate(tmdbMovie);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException("Invalid TMDB movie " + tmdbId, violations);
    }
    LOGGER.infof("Found: %s", tmdbMovie);    
    return tmdbMovie;    
  }
  
  private void ingest(int tmdbId) {
    statusEmitter.send(MovieStatus.of(tmdbId, MessageType.INGEST, MessageStatus.RECEIVED));

    // Get the movie details plus cast and crew from TMDB.
    var tmdbMovie = findById(tmdbId);
    statusEmitter.send(MovieStatus.of(tmdbId, MessageType.INGEST, MessageStatus.EXTRACTED));

    // Send the command to create the movie to EMDB.
    var command = createMessage(tmdbMovie);
    createEmitter.send(command);
  }

}
