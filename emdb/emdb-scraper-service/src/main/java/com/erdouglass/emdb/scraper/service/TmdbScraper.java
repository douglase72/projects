package com.erdouglass.emdb.scraper.service;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.PersonCreateDto;
import com.erdouglass.emdb.common.message.IngestMessage;
import com.erdouglass.emdb.common.message.JobMessage;
import com.erdouglass.emdb.common.message.JobMessage.JobSource;
import com.erdouglass.emdb.common.message.JobMessage.JobStatus;
import com.erdouglass.emdb.scraper.client.TmdbPersonClient;
import com.erdouglass.emdb.scraper.mapper.TmdbPersonMapper;
import com.erdouglass.emdb.scraper.query.TmdbPersonDto;
import com.google.common.util.concurrent.RateLimiter;

import io.micrometer.core.instrument.MeterRegistry;
import io.opentelemetry.context.Context;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

/// Abstract base class for TMDB ingestion services.
///
/// This class encapsulates shared dependencies and configuration required by specific
/// scraping implementations (e.g., Movies, TV Shows). It provides access to rate limits,
/// external clients for person data, and validation logic to ensure data integrity
/// before processing.
public abstract class TmdbScraper {
  private static final Logger LOGGER = Logger.getLogger(TmdbScraper.class);
  
  /// The maximum number of cast members to ingest per entity.
  /// Defined by the property {@code tmdb.cast.limit}.
  @Inject
  @ConfigProperty(name = "tmdb.cast.limit")
  Integer castLimit;

  /// The maximum number of crew members to ingest per entity.
  /// Defined by the property {@code tmdb.crew.limit}.
  @Inject
  @ConfigProperty(name = "tmdb.crew.limit")
  Integer crewLimit;
  
  /// The maximum number of requests allowed per second for person details.
  /// Defined by the property {@code tmdb.rate.limit}.
  @Inject
  @ConfigProperty(name = "tmdb.rate.limit")
  Integer rateLimit;
  
  @Inject
  @Channel("job-log-out")
  Emitter<JobMessage> jobEmitter;
  
  /// Client for interacting with the TMDB Person API endpoints.
  @Inject
  @RestClient
  TmdbPersonClient personClient;
  
  /// Mapper for converting TMDB person DTOs into domain messages.
  @Inject
  TmdbPersonMapper personMapper;
  
  @Inject
  MeterRegistry registry;
  
  /// Validator for ensuring ingested data meets constraints before processing.
  @Inject
  Validator validator;
  
  /// Processes an incoming ingestion message.
  ///
  /// Implementations must define the specific workflow for fetching, enriching,
  /// and publishing data for the specific media type.
  ///
  /// @param message the {@link IngestMessage} containing the ID of the entity to scrape.
  public abstract void onMessage(IngestMessage message);
  
  protected Map<Integer, PersonCreateDto> findPeople(Stream<Integer> ids, int movieId, UUID jobId) {
    var rateLimiter = RateLimiter.create(rateLimit);
    var people = ids.distinct()
        .map(id -> {
          rateLimiter.acquire(); 
          return personMapper.toPersonCreateDto(findPerson(id));
        })
        .collect(Collectors.toMap(PersonCreateDto::tmdbId, Function.identity()));
    var msg = String.format("Fetched %d people for TMDB movie", people.size());
    updateProgress(jobId, movieId, JobStatus.PROGRESS, msg, 70);
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
  protected void updateProgress(UUID id, int tmdbId, JobStatus status, String message, int progress) {
    try {
      var ctx = Context.current();
      Thread.ofVirtual().start(ctx.wrap(() -> {
        var jobMessage = JobMessage.builder()
            .id(id)
            .tmdbId(tmdbId)
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
