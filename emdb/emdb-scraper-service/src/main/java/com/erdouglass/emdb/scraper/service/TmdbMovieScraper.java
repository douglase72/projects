package com.erdouglass.emdb.scraper.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
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

import com.erdouglass.emdb.common.CreditType;
import com.erdouglass.emdb.common.MovieCreditCreateDto;
import com.erdouglass.emdb.common.PersonCreateDto;
import com.erdouglass.emdb.common.message.IngestMessage;
import com.erdouglass.emdb.common.message.JobMessage.JobStatus;
import com.erdouglass.emdb.common.message.MovieCreateMessage;
import com.erdouglass.emdb.scraper.client.TmdbMovieClient;
import com.erdouglass.emdb.scraper.mapper.TmdbPersonMapper;
import com.erdouglass.emdb.scraper.query.TmdbMovieDto;
import com.erdouglass.emdb.scraper.query.TmdbMovieDto.CastCredit;
import com.erdouglass.emdb.scraper.query.TmdbMovieDto.CrewCredit;

import io.micrometer.core.instrument.Timer;
import io.smallrye.reactive.messaging.annotations.Blocking;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

/// Orchestrator for scraping Movie data from The Movie Database (TMDB).
///
/// This service consumes ingestion requests, fetches raw data from external APIs,
/// validates the structure, and aggregates the results into a unified {@link MovieCreateMessage}.
/// It handles the complexity of "fan-out" requests where a single movie requires
/// subsequent calls to fetch detailed profiles for every cast and crew member.
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
  TmdbPersonMapper mapper;
  
  /// Ingest the TMDB movie identified in the given message.
  ///
  /// This method executes the following workflow:
  /// 1.  **Validation**: Verifies the incoming message structure.
  /// 2.  **Job Tracking**: Emits a `STARTED` status to the job log.
  /// 3.  **Movie Fetch**: Retrieves the core movie details and credit IDs from TMDB.
  /// 4.  **Credit Expansion**: Iterates through cast and crew to fetch detailed person profiles.
  ///     (This step is rate-limited to prevent API throttling).
  /// 5.  **Completion**: Compiles all data into a {@link MovieCreateMessage} and publishes
  ///     it to the `movie-create-out` channel.
  /// 
  /// **Note**
  /// This method takes one message at a time from the movie-ingest queue and scrapes 
  /// TMDB for the relevant data including movie details, movie credits, and movie cast
  /// and crew. This can be a long running task that gets off loaded to a platform thread
  /// so that the event I/O thread does not get blocked.
  ///
  /// @param message The {@link IngestMessage} containing the TMDB ID to be scraped.
  @Override
  @Blocking
  @Incoming("movie-ingest-in")
  @Retry(maxRetries = 3, abortOn = { ConstraintViolationException.class })
  public void onMessage(IngestMessage message) {
    var jobId = message.id();
    var tmdbId = message.tmdbId();
    var latency = Duration.between(message.timestamp(), Instant.now()).toMillis();
    LOGGER.infof("Message: %s, movie-ingest queue latency: %d ms", message, latency);
    
    try {
      var violations = validator.validate(message);
      if (!violations.isEmpty()) {
        throw new ConstraintViolationException(violations);
      }
      updateProgress(jobId, tmdbId, JobStatus.STARTED, "TMDB movie ingest started", 1);
      
      var timer = Timer.builder("scraper.method.duration")
          .description("Measures the time to scrape a movie from TMDB")
          .tag("class", "TmdbMovieScraper") 
          .tag("method", "scrape") 
          .register(registry);
      timer.record(() -> scrape(tmdbId, jobId));
    } catch (ConstraintViolationException e) {
      var msg = String.format("Failed to validate message %s", message);
      updateProgress(jobId, tmdbId, JobStatus.FAILED, msg, 0);
      throw new RuntimeException(msg, e);
    } catch (Exception e) {
      var msg = "Failed to ingest movie from TMDB";
      updateProgress(jobId, tmdbId, JobStatus.FAILED, msg, 0);
      throw new RuntimeException(msg, e);
    }
  }
  
  private List<MovieCreditCreateDto> createCredits(TmdbMovieDto movie, Map<Integer, PersonCreateDto> people) {
    var cast = movie.credits().cast().stream()
        .limit(castLimit)
        .map(c -> MovieCreditCreateDto.builder()
            .tmdbId(c.credit_id())
            .type(CreditType.CAST)
            .role(c.character())
            .person(people.get(c.id()))
            .order(c.order())
            .build());
    var crew = movie.credits().crew().stream()
        .limit(crewLimit)
        .map(c -> MovieCreditCreateDto.builder()
            .tmdbId(c.credit_id())
            .type(CreditType.CREW)
            .role(c.job())
            .person(people.get(c.id()))
            .build());
    var credits = Stream.concat(cast, crew).toList();
    LOGGER.info(String.format("Found %d credits in TMDB movie %d", credits.size(), movie.id()));
    return credits;    
  }
  
  /// Fetches core movie details from the TMDB API.
  ///
  /// @param tmdbId the external TMDB ID.
  /// @param jobId  the current job correlation ID for progress updates.
  /// @return the validated {@link TmdbMovieDto}.
  private TmdbMovieDto findMovie(int tmdbId, UUID jobId) {
    var timer = Timer.builder("scraper.method.duration")
        .description("Measures the time to fetch the movie from TMDB")
        .tag("class", "TmdbMovieScraper")
        .tag("method", "findMovie") 
        .register(registry);
    var tmdbMovie = timer.record(() -> client.findById(tmdbId, CREDITS));
    var violations = validator.validate(tmdbMovie);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
    updateProgress(jobId, tmdbId, JobStatus.PROGRESS, "Fetched movie from TMDB", 30);
    LOGGER.infof("Fetched: %s", tmdbMovie);
    return tmdbMovie;
  }
  
  private void scrape(int tmdbId, UUID jobId) {
    var movie = findMovie(tmdbId, jobId);
    var timer = Timer.builder("scraper.method.duration")
        .description("Measures the time to fetch people from TMDB")
        .tag("class", "TmdbMovieScraper")
        .tag("method", "findPeople") 
        .register(registry);
    var ids = Stream.concat(
        movie.credits().cast().stream().limit(castLimit).map(CastCredit::id), 
        movie.credits().crew().stream().limit(crewLimit).map(CrewCredit::id));
    var people = timer.record(() -> findPeople(ids, tmdbId, jobId));
    var credits = createCredits(movie, people);
    sendMessage(movie, credits, jobId);
  }
  
  /// Assembles and publishes the final {@link MovieCreateMessage}.
  ///
  /// @param movie   the core movie data.
  /// @param credits the list of cast and crew credits.
  /// @param people  the list of detailed person profiles.
  /// @param jobId   the correlation ID.
  private void sendMessage(TmdbMovieDto movie, List<MovieCreditCreateDto> credits, UUID jobId) {
    updateProgress(jobId, movie.id(), JobStatus.PROGRESS, "TMDB movie queued for persistence", 71);
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
        .build(); 
    createEmitter.send(Message.of(createMessage)
        .addMetadata(OutgoingRabbitMQMetadata.builder()
        .withRoutingKey(CREATE_KEY)
        .build())); 
    LOGGER.infof("Sent: %s", createMessage);  
  }

}
