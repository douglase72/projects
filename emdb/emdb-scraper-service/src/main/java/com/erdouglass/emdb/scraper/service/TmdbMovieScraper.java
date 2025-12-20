package com.erdouglass.emdb.scraper.service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.message.IngestMessage;
import com.erdouglass.emdb.common.message.JobMessage;
import com.erdouglass.emdb.common.message.JobMessage.JobSource;
import com.erdouglass.emdb.common.message.JobMessage.JobStatus;
import com.erdouglass.emdb.common.message.MovieCreateMessage;
import com.erdouglass.emdb.scraper.client.TmdbMovieClient;
import com.erdouglass.emdb.scraper.query.TmdbMovieDto;

import io.opentelemetry.context.Context;
import io.smallrye.reactive.messaging.annotations.Blocking;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class TmdbMovieScraper {
  private static final Logger LOGGER = Logger.getLogger(TmdbMovieScraper.class);
  private static final String CREATE_KEY = "movie.create";
  private static final String CREDITS = "credits";
  
  @Inject
  @RestClient
  TmdbMovieClient client;
  
  @Inject
  @Channel("job-log-out")
  Emitter<JobMessage> jobEmitter;
  
  @Inject
  @Channel("movie-create-out") 
  Emitter<MovieCreateMessage> createEmitter;
  
  @Inject
  Validator validator;
  
  @Blocking
  @Incoming("movie-ingest-in")
  public void onMessage(IngestMessage message) {
    var jobId = message.id();
    var tmdbId = message.tmdbId();
    var latency = Duration.between(message.timestamp(), Instant.now()).toMillis();
    LOGGER.infof("Message: %s, latency: %d ms", message, latency);
    
    try {
      var msg = String.format("Ingest started for TMDB movie %d", tmdbId);
      updateProgress(jobId, JobStatus.STARTED, msg, 1);
      
      var tmdbMovie = findMovie(tmdbId, jobId);
      
      Thread.sleep(3000);
      msg = String.format("Fetched %d people for TMDB movie %d", 359, message.tmdbId());
      updateProgress(jobId, JobStatus.PROGRESS, msg, 70);
      
      // Send create movie message to media service
      sendMessage(tmdbMovie, jobId);
    } catch (Exception e) {
      var msg = String.format("Failed to scrape TMDB movie %d", tmdbId);
      updateProgress(jobId, JobStatus.FAILED, msg, 0);
      LOGGER.error(msg, e);
    }
  }
  
  private void sendMessage(TmdbMovieDto movie, UUID jobId) {
    var msg = String.format("TMDB movie %d queued for persistence", movie.id());
    updateProgress(jobId, JobStatus.PROGRESS, msg, 71);
    var createMessage = MovieCreateMessage.builder()
        .id(jobId)
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
    createEmitter.send(Message.of(createMessage)
        .addMetadata(OutgoingRabbitMQMetadata.builder()
        .withRoutingKey(CREATE_KEY)
        .build())); 
    LOGGER.infof("Sent: %s", createMessage);  
  }
  
  private TmdbMovieDto findMovie(int tmdbId, UUID jobId) {
    var tmdbMovie = client.findById(tmdbId, CREDITS);
    var violations = validator.validate(tmdbMovie);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
    var msg = String.format("Fetched TMDB movie %d details", tmdbId);
    updateProgress(jobId, JobStatus.PROGRESS, msg, 30);
    LOGGER.infof("Found: %s", tmdbMovie);
    return tmdbMovie;
  }
  
  /// Publishes an asynchronous status update to the `audit-trail-out` channel.
  ///
  /// **Note**
  /// By default, SmallRye Reactive Messaging buffers messages emitted during the execution 
  /// of an {@code @Incoming} method until that method completes. This behavior delays 
  /// UI progress bars.
  ///
  /// To bypass this buffering and send the status **immediately**, this method:
  /// 1. Captures the current {@link Context} (to preserve OpenTelemetry/MDC traces).
  /// 2. Spawns a **Virtual Thread**.
  /// 3. Executes the emit operation within that isolated thread, forcing an immediate flush 
  ///    to the broker.
  ///
  private void updateProgress(
      UUID id, JobStatus status, String message, Integer progress) {
    try {
      var ctx = Context.current();
      Thread.ofVirtual().start(ctx.wrap(() -> {
        var jobMessage = JobMessage.builder()
            .id(id)
            .source(JobSource.SCRAPER)
            .status(status)
            .content(message)
            .progress(progress)
            .build();
        jobEmitter.send(Message.of(jobMessage).addMetadata(OutgoingRabbitMQMetadata.builder()
            .withRoutingKey(Configuration.JOB_KEY)
            .build()));
      })).join();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } 
  }

}
