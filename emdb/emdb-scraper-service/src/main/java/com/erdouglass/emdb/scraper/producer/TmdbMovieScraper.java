package com.erdouglass.emdb.scraper.producer;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.command.MovieCreateCommand;
import com.erdouglass.emdb.common.command.MovieCreditCreateCommand;
import com.erdouglass.emdb.common.command.PersonCreateCommand;
import com.erdouglass.emdb.common.query.MovieStatus;
import com.erdouglass.emdb.common.query.MovieStatus.MessageStatus;
import com.erdouglass.emdb.common.query.MovieStatus.MessageType;
import com.erdouglass.emdb.scraper.anno.LogValidation;
import com.erdouglass.emdb.scraper.client.TmdbMovieClient;
import com.erdouglass.emdb.scraper.dto.TmdbMovie;
import com.erdouglass.emdb.scraper.mapper.TmdbMovieCreditMapper;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.vertx.core.json.JsonObject;

/// Orchestrates the scraping of Movie data from TMDB.
///
/// This processor handles:
/// - Fetching core movie details.
/// - Fetching associated credits (Cast & Crew).
/// - Fetching person details for those credits.
/// - Aggregating all data into a `MovieCreateCommand`.
@ApplicationScoped
public class TmdbMovieScraper extends TmdbScraper {
  private static final Logger LOGGER = Logger.getLogger(TmdbMovieScraper.class);
  private static final String CREDITS = "credits";
  
  @Inject
  @RestClient
  TmdbMovieClient client;
  
  @Inject
  @Channel("movie-create-out") 
  Emitter<MovieCreateCommand> createEmitter;
  
  @Inject
  TmdbMovieCreditMapper mapper;
  
  @Inject
  @Channel("movie-status-out") 
  Emitter<MovieStatus> statusEmitter;
  
  /// Entry point for the scraping process.
  ///
  /// Listens to the `movie-request-in` channel and delegates to the appropriate
  /// handler based on the message type.
  ///
  /// @param jsonObject The raw JSON message received from the queue.
  @Override
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
  
  /// Builds the final command object to create the movie in the domain.
  ///
  /// @param movie The raw movie data from TMDB.
  /// @param credits The list of cast and crew commands.
  /// @param people The list of person commands involved in the movie.
  /// @return A fully populated `MovieCreateCommand`.  
  private MovieCreateCommand createMessage(
      TmdbMovie movie,
      List<MovieCreditCreateCommand> credits, 
      List<PersonCreateCommand> people) {
    return MovieCreateCommand.builder()
        .tmdbId(movie.id())
        .title(movie.title())
        .releaseDate(movie.release_date())
        //.score(movie.vote_average())
        .score(-1f)
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
  }
  
  /// Fetches a movie by ID from the TMDB API and validates the response.
  ///
  /// @param tmdbId The ID of the movie to fetch.
  /// @return The validated `TmdbMovie` DTO.
  /// @throws ConstraintViolationException If the API response fails validation.  
  private TmdbMovie findById(int tmdbId) {
    var tmdbMovie = client.findById(tmdbId, CREDITS);
    Set<ConstraintViolation<TmdbMovie>> violations = validator.validate(tmdbMovie);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException("Invalid TMDB movie " + tmdbId, violations);
    }
    LOGGER.infof("Found: %s", tmdbMovie);    
    return tmdbMovie;    
  }

  /// Extracts and maps the credits (Cast & Crew) from the movie DTO.
  ///
  /// Limits the number of cast and crew members based on configuration.
  ///
  /// @param movie The movie DTO containing raw credit data.
  /// @return A list of `MovieCreditCreateCommand` objects.
  private List<MovieCreditCreateCommand> findCredits(TmdbMovie movie) {
    var cast = movie.credits().cast().stream()
        .limit(castLimit)
        .map(mapper::toCastCredit);
    var crew = movie.credits().crew().stream()
        .limit(crewLimit)
        .map(mapper::toCrewCredit);
    var credits = Stream.concat(cast, crew).toList();
    LOGGER.infof("Found: %d credits in %s", credits.size(), movie);
    return credits;
  }
  
  /// Fetches details for all unique people found in the movie's credits.
  ///
  /// @param movie The movie context (used for logging).
  /// @param credits The list of processed credits to extract Person IDs from.
  /// @return A list of `PersonCreateCommand` objects.
  private List<PersonCreateCommand> findPeople(TmdbMovie movie, List<MovieCreditCreateCommand> credits) {
    var people = credits.stream()
        .map(MovieCreditCreateCommand::id)
        .distinct()
        .map(id -> personMapper.toPersonCreateCommand(personClient.findById(id)))
        .toList();
    LOGGER.infof("Found: %d people in %s", people.size(), movie);
    return people;
  }
  
  /// Orchestrates the full ingestion flow for a single movie ID.
  ///
  /// 1. Fetches Movie details.
  /// 2. Fetches Credits.
  /// 3. Fetches People.
  /// 4. Sends a single aggregated command to the `movie-create-out` channel.
  ///
  /// @param tmdbId The ID of the movie to ingest.
  private void ingest(int tmdbId) {
    statusEmitter.send(MovieStatus.of(tmdbId, MessageType.INGEST, MessageStatus.RECEIVED));

    // Get the movie details plus cast and crew from TMDB.
    var tmdbMovie = findById(tmdbId);
    var credits = findCredits(tmdbMovie);
    var people = findPeople(tmdbMovie, credits);
    statusEmitter.send(MovieStatus.of(tmdbId, MessageType.INGEST, MessageStatus.EXTRACTED));

    // Send the command to create the movie in EMDB.
    var command = createMessage(tmdbMovie, credits, people);
    createEmitter.send(command);
  }

}
