package com.erdouglass.emdb.scraper.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.CreditType;
import com.erdouglass.emdb.common.MovieCreditCreateDto;
import com.erdouglass.emdb.common.PersonCreateDto;
import com.erdouglass.emdb.common.message.IngestMessage;
import com.erdouglass.emdb.common.message.MovieCreateMessage;
import com.erdouglass.emdb.scraper.client.TmdbMovieClient;
import com.erdouglass.emdb.scraper.query.TmdbMovieDto;
import com.erdouglass.emdb.scraper.query.TmdbMovieDto.CastCredit;
import com.erdouglass.emdb.scraper.query.TmdbMovieDto.CrewCredit;

import io.micrometer.core.annotation.Timed;
import io.opentelemetry.api.baggage.Baggage;
import io.smallrye.reactive.messaging.annotations.Blocking;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

/// Scrapes movie data from The Movie Database (TMDB).
///
/// This service starts the movie ingestion process by scraping The Movie Database (TMDB).
/// This is a singleton service to ensure the TMDB rate limit is respected. Multiple 
/// replicas will violate that rate limit.
@ApplicationScoped
public class TmdbMovieScraper extends TmdbScraper {
  private static final Logger LOGGER = Logger.getLogger(TmdbMovieScraper.class);
  private static final String CREATE_KEY = "movie.create";
  private static final String CREDITS = "credits";
  
  @Inject
  @RestClient
  TmdbMovieClient client;
  
  /// Extracts the given movie from TMDB.
  /// 
  /// This method takes one message at a time from the movie-ingest queue and scrapes
  /// TMDB for the relevant movie data. The @Blocking annotation is used to ensure the
  /// process is performed on a separate platform thread so that the event loop doesn't
  /// get blocked. 
  ///
  /// @param message the {@link IngestMessage} to consume from the movie-ingest queue.
  /// @return the {@link MovieCreateMessage} for the emdb-media-service to load into
  ///         the database.
  @Blocking
  @Incoming("movie-ingest-in")
  @Outgoing("movie-create-out")
  @Timed(
      value = "emdb.method.duration", 
      extraTags = {"method", "Movie Scrape"}
  )
  public Message<MovieCreateMessage> onMessage(Message<IngestMessage> wrapper) {
    var message = wrapper.getPayload();
    var jobId = Baggage.current().getEntryValue("job-id");
    LOGGER.infof("Received: %s for TMDB movie %d", jobId, message.tmdbId());
    
    try {
      var start = System.nanoTime();    
      var movie = findMovie(message.tmdbId());
      var ids = Stream.concat(
          movie.credits().cast().stream().limit(castLimit).map(CastCredit::id), 
          movie.credits().crew().stream().limit(crewLimit).map(CrewCredit::id));
      var people = findPeople(ids, message.tmdbId());
      var credits = createCredits(movie, people);      
      var et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
      LOGGER.infof("Scraped TMDB movie %d in %d ms", message.tmdbId(), et);
      return createMessage(movie, credits, jobId).withAck(() -> wrapper.ack());
    } catch (Exception e) {
      var msg = String.format("Failed to ingest TMDB movie %d", message.tmdbId());
      LOGGER.error(msg, e);
      wrapper.nack(e);
    }
    return null;
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
  
  private Message<MovieCreateMessage> createMessage(
      TmdbMovieDto movie, List<MovieCreditCreateDto> credits, String jobId) {
    var message = MovieCreateMessage.builder()
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
    LOGGER.infof("Sent: %s for TMDB movie %d", jobId, message.tmdbId());
    return Message.of(message)
        .addMetadata(OutgoingRabbitMQMetadata.builder()
            .withRoutingKey(CREATE_KEY)
            .build());
  }
  
  private TmdbMovieDto findMovie(int tmdbId) {
    var start = System.nanoTime();
    var movie = client.findById(tmdbId, CREDITS);
    var et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    LOGGER.infof("Fetched TMDB movie %d in %d ms", movie.id(), et);
    return movie;
  }

}
