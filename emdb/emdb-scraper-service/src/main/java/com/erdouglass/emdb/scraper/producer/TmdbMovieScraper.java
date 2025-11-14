package com.erdouglass.emdb.scraper.producer;

import java.util.List;
import java.util.stream.Stream;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.command.MovieCreditCreateCommand;
import com.erdouglass.emdb.common.command.MovieMessage;
import com.erdouglass.emdb.common.command.PersonCreateCommand;
import com.erdouglass.emdb.scraper.client.TmdbMovieClient;
import com.erdouglass.emdb.scraper.client.TmdbPersonClient;
import com.erdouglass.emdb.scraper.dto.TmdbMovie;
import com.erdouglass.emdb.scraper.mapper.TmdbCreditMapper;
import com.erdouglass.emdb.scraper.mapper.TmdbMovieMapper;
import com.erdouglass.emdb.scraper.mapper.TmdbPersonMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TmdbMovieScraper {
  private static final Logger LOGGER = Logger.getLogger(TmdbMovieScraper.class);
  private static final String CREDITS = "credits";
  
  @Inject
  @ConfigProperty(name = "tmdb.cast.limit")
  Integer castLimit;

  @Inject
  @ConfigProperty(name = "tmdb.crew.limit")
  Integer crewLimit;
  
  @Inject
  TmdbCreditMapper creditMapper;

  @Inject
  @Channel("movies")
  Emitter<MovieMessage> emitter;
  
  @Inject
  @RestClient
  TmdbMovieClient movieClient;
  
  @Inject
  TmdbMovieMapper movieMapper;
  
  @Inject
  @RestClient
  TmdbPersonClient personClient;
  
  @Inject
  TmdbPersonMapper personMapper;

  public void ingest(int tmdbId) {
    LOGGER.infof("Ingesting TMDB movie id: %d", tmdbId);
    var tmdbMovie = findMovie(tmdbId);
    var credits = findCredits(tmdbMovie);
    var people = findPeople(credits);
    var message = new MovieMessage(movieMapper.toMovieCreateCommand(tmdbMovie), credits, people);
    emitter.send(message);
    LOGGER.infof("Sent: %s", message);
  }

  public void synchronize(long emdbId, int tmdbId) {
    LOGGER.infof("Synchronizing EMDB movie id: %d with TMDB movie id: %d", emdbId, tmdbId);
  }
  
  private List<MovieCreditCreateCommand> findCredits(TmdbMovie movie) {
    var cast = movie.credits().cast().stream()
        .limit(castLimit)
        .map(creditMapper::toCastCredit);
    var crew = movie.credits().crew().stream()
        .limit(crewLimit)
        .map(creditMapper::toCrewCredit);
    var credits = Stream.concat(cast, crew).toList();
    LOGGER.infof("Found: %d credits", credits.size());
    return credits;
  }

  private TmdbMovie findMovie(int tmdbId) {
    var tmdbMovie = movieClient.findById(tmdbId, CREDITS);
    LOGGER.infof("Found: %s", tmdbMovie);    
    return tmdbMovie;
  }

  private List<PersonCreateCommand> findPeople(List<MovieCreditCreateCommand> credits) {
    var people = credits.stream()
        .map(MovieCreditCreateCommand::personId)
        .distinct()
        .map(id -> personMapper.toPersonCreateCommand(personClient.findById(id.intValue())))
        .toList();
    LOGGER.infof("Found: %d people", people.size());
    return people;
  }

}
