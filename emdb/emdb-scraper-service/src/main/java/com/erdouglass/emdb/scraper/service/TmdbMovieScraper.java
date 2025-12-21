package com.erdouglass.emdb.scraper.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;

import org.eclipse.microprofile.faulttolerance.Retry;
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
import com.erdouglass.emdb.common.message.MovieCreditCreateMessage;
import com.erdouglass.emdb.common.message.PersonCreateMessage;
import com.erdouglass.emdb.scraper.client.TmdbMovieClient;
import com.erdouglass.emdb.scraper.mapper.TmdbMovieCreditMapper;
import com.erdouglass.emdb.scraper.query.TmdbMovieDto;
import com.erdouglass.emdb.scraper.query.TmdbPersonDto;
import com.google.common.util.concurrent.RateLimiter;

import io.opentelemetry.context.Context;
import io.smallrye.reactive.messaging.annotations.Blocking;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class TmdbMovieScraper extends TmdbScraper {
  private static final Logger LOGGER = Logger.getLogger(TmdbMovieScraper.class);
  private static final String CREATE_KEY = "movie.create";
  private static final String CREDITS = "credits";
  
  @Inject
  @RestClient
  TmdbMovieClient client;
  
  @Inject
  @Channel("movie-create-out") 
  Emitter<MovieCreateMessage> createEmitter;
  
  @Inject
  @Channel("job-log-out")
  Emitter<JobMessage> jobEmitter;
  
  @Inject
  TmdbMovieCreditMapper mapper;
  
  @Override
  @Blocking
  @Incoming("movie-ingest-in")
  @Retry(maxRetries = 3, abortOn = { ConstraintViolationException.class })
  public void onMessage(IngestMessage message) {
    var jobId = message.id();
    var tmdbId = message.tmdbId();
    var latency = Duration.between(message.timestamp(), Instant.now()).toMillis();
    LOGGER.infof("Message: %s, latency: %d ms", message, latency);
    
    try {
      var violations = validator.validate(message);
      if (!violations.isEmpty()) {
        throw new ConstraintViolationException(violations);
      }
      var msg = String.format("Ingest started for TMDB movie %d", tmdbId);
      updateProgress(jobId, JobStatus.STARTED, msg, 1);
      
      // Send the message to create the movie to the media service.
      var tmdbMovie = findMovie(tmdbId, jobId);
      var credits = findCredits(tmdbMovie);
      var people = findPeople(credits, tmdbId, jobId);
      sendMessage(tmdbMovie, credits, people, jobId);
    } catch (ConstraintViolationException e) {
      var msg = String.format("Failed to validate message %s", message);
      updateProgress(jobId, JobStatus.FAILED, msg, 0);
      throw new RuntimeException(msg, e);
    } catch (Exception e) {
      var msg = String.format("Failed to scrape TMDB movie %d", tmdbId);
      updateProgress(jobId, JobStatus.FAILED, msg, 0);
      throw new RuntimeException(msg, e);
    }
  }
  
  private List<MovieCreditCreateMessage> findCredits(TmdbMovieDto movie) {
    var cast = movie.credits().cast().stream()
        .limit(castLimit)
        .map(mapper::toCastCredit);
    var crew = movie.credits().crew().stream()
        .limit(crewLimit)
        .map(mapper::toCrewCredit);
    var credits = Stream.concat(cast, crew).toList();
    LOGGER.info(String.format("Found %d credits in TMDB movie %d", credits.size(), movie.id()));
    return credits;    
  }
  
  private TmdbMovieDto findMovie(int tmdbId, UUID jobId) {
    var tmdbMovie = client.findById(tmdbId, CREDITS);
    var violations = validator.validate(tmdbMovie);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
    var msg = String.format("Fetched TMDB movie %d details", tmdbId);
    updateProgress(jobId, JobStatus.PROGRESS, msg, 30);
    LOGGER.infof("Fetched: %s", tmdbMovie);
    return tmdbMovie;
  }
  
  private List<PersonCreateMessage> findPeople(List<MovieCreditCreateMessage> credits, int tmdbId, UUID jobId) {
    var rateLimiter = RateLimiter.create(rateLimit); 
    var people = credits.stream()
        .map(MovieCreditCreateMessage::id)
        .distinct()
        .map(id -> {
          rateLimiter.acquire(); 
          return personMapper.toPersonCreateCommand(findPerson(id));
        })
        .toList();
    var msg = String.format("Fetched %d people for TMDB movie %d", people.size(), tmdbId);
    updateProgress(jobId, JobStatus.PROGRESS, msg, 70);
    LOGGER.info(msg);
    return people;
  }
  
  private TmdbPersonDto findPerson(int tmdbId) {
    var tmdbPerson = personClient.findById(tmdbId);
    var violations = validator.validate(tmdbPerson);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
    return tmdbPerson;
  }
  
  private void sendMessage(
      TmdbMovieDto movie, 
      List<MovieCreditCreateMessage> credits, 
      List<PersonCreateMessage> people,
      UUID jobId) {
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
        .credits(credits)
        .people(people)
        .build(); 
    createEmitter.send(Message.of(createMessage)
        .addMetadata(OutgoingRabbitMQMetadata.builder()
        .withRoutingKey(CREATE_KEY)
        .build())); 
    LOGGER.infof("Sent: %s", createMessage);  
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
  ///    to the broker
  private void updateProgress(UUID id, JobStatus status, String message, Integer progress) {
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
