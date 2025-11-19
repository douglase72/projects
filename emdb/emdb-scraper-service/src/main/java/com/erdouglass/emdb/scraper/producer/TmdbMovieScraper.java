package com.erdouglass.emdb.scraper.producer;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.command.MovieCreateCommand;
import com.erdouglass.emdb.common.command.MovieCreditCreateCommand;
import com.erdouglass.emdb.common.command.PersonCreateCommand;
import com.erdouglass.emdb.scraper.client.TmdbMovieClient;
import com.erdouglass.emdb.scraper.dto.TmdbMovie;
import com.erdouglass.emdb.scraper.mapper.TmdbMovieCreditMapper;

@ApplicationScoped
public class TmdbMovieScraper extends TmdbScraper<TmdbMovie> {
  private static final Logger LOGGER = Logger.getLogger(TmdbMovieScraper.class);
  private static final String CREDITS = "credits";
  
  @Inject
  TmdbMovieCreditMapper creditMapper;

  @Inject
  @Channel("movies")
  Emitter<MovieCreateCommand> emitter;
  
  @Inject
  @RestClient
  TmdbMovieClient movieClient;

  @Override
  public void ingest(@NotNull @Positive Integer tmdbId) {
    LOGGER.infof("Ingesting TMDB movie id: %d", tmdbId);
    var tmdbMovie = findMovie(tmdbId);
    var credits = findCredits(tmdbMovie);
    var people = findPeople(tmdbMovie, credits);    
    var message = createMessage(tmdbMovie, credits, people);
    emitter.send(message);
    LOGGER.infof("Sent: %s", message);
  }

  @Override
  public void synchronize(@NotNull @Positive Long emdbId, @NotNull @Positive Integer tmdbId) {
    LOGGER.infof("Synchronizing EMDB movie id: %d with TMDB movie id: %d", emdbId, tmdbId);
  }
  
  private MovieCreateCommand createMessage(
      TmdbMovie movie, List<MovieCreditCreateCommand> credits, List<PersonCreateCommand> people) {
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
        .credits(credits)
        .people(people)
        .build();
  }
  
  private List<MovieCreditCreateCommand> findCredits(TmdbMovie movie) {
    var cast = movie.credits().cast().stream()
        .limit(castLimit)
        .map(creditMapper::toCastCredit);
    var crew = movie.credits().crew().stream()
        .limit(crewLimit)
        .map(creditMapper::toCrewCredit);
    var credits = Stream.concat(cast, crew).toList();
    LOGGER.infof("Found: %d credits in %s", credits.size(), movie);
    return credits;
  }

  private TmdbMovie findMovie(int tmdbId) {
    var tmdbMovie = movieClient.findById(tmdbId, CREDITS);
    Set<ConstraintViolation<TmdbMovie>> violations = validator.validate(tmdbMovie);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException("Invalid TMDB movie " + tmdbId, violations);
    }
    LOGGER.infof("Found: %s", tmdbMovie);    
    return tmdbMovie;
  }

  private List<PersonCreateCommand> findPeople(TmdbMovie movie, List<MovieCreditCreateCommand> credits) {
    var people = credits.stream()
        .map(MovieCreditCreateCommand::id)
        .distinct()
        .map(id -> personMapper.toPersonCreateCommand(personClient.findById(id)))
        .toList();
    LOGGER.infof("Found: %d people in %s", people.size(), movie);
    return people;
  }

}
