package com.erdouglass.emdb.scraper.producer;

import java.util.List;
import java.util.stream.Stream;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.command.PersonCreateCommand;
import com.erdouglass.emdb.common.command.SeriesCreateCommand;
import com.erdouglass.emdb.common.command.SeriesCreditCreateCommand;
import com.erdouglass.emdb.scraper.client.TmdbPersonClient;
import com.erdouglass.emdb.scraper.client.TmdbSeriesClient;
import com.erdouglass.emdb.scraper.dto.TmdbSeries;
import com.erdouglass.emdb.scraper.mapper.TmdbPersonMapper;
import com.erdouglass.emdb.scraper.mapper.TmdbSeriesCreditMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@ApplicationScoped
public class TmdbSeriesScraper {
  private static final Logger LOGGER = Logger.getLogger(TmdbMovieScraper.class);
  private static final String CREDITS = "aggregate_credits";
  
  @Inject
  @ConfigProperty(name = "tmdb.cast.limit")
  Integer castLimit;

  @Inject
  @ConfigProperty(name = "tmdb.crew.limit")
  Integer crewLimit;
  
  @Inject
  TmdbSeriesCreditMapper creditMapper;
  
  @Inject
  @Channel("series")
  Emitter<SeriesCreateCommand> emitter;
  
  @Inject
  @RestClient
  TmdbPersonClient personClient;
  
  @Inject
  TmdbPersonMapper personMapper;
  
  @Inject
  @RestClient
  TmdbSeriesClient seriesClient;

  public void ingest(@NotNull @Positive Integer tmdbId) {
    LOGGER.infof("Ingesting TMDB series id: %d", tmdbId);
    var tmdbSeries = findSeries(tmdbId);
    var credits = findCredits(tmdbSeries);
    var people = findPeople(tmdbSeries, credits);
    var message = createMessage(tmdbSeries, credits, people);
    emitter.send(message);
    LOGGER.infof("Sent: %s", message);
  }
  
  public void synchronize(@NotNull @Positive Long emdbId, @NotNull @Positive Integer tmdbId) {
    LOGGER.infof("Synchronizing EMDB series id: %d with TMDB series id: %d", emdbId, tmdbId);
  }
  
  private SeriesCreateCommand createMessage(
      TmdbSeries series, List<SeriesCreditCreateCommand> credits, List<PersonCreateCommand> people) {
    return SeriesCreateCommand.builder()
        .tmdbId(series.id())
        .name(series.name())
        .score(series.vote_average())
        .status(series.status())
        .homepage(series.homepage())
        .originalLanguage(series.original_language())
        .backdrop(series.backdrop_path())
        .poster(series.poster_path())
        .tagline(series.tagline())
        .overview(series.overview()) 
        .credits(credits)
        .people(people)
        .build();
  }
  
  private List<SeriesCreditCreateCommand> findCredits(TmdbSeries series) {
    var cast = series.aggregate_credits().cast().stream()
        .limit(castLimit)
        .map(creditMapper::toCastCredit);
    var crew = series.aggregate_credits().crew().stream()
        .limit(crewLimit)
        .map(creditMapper::toCrewCredit);
    var credits = Stream.concat(cast, crew).toList();
    LOGGER.infof("Found: %d credits in %s", credits.size(), series);
    return credits;
  }
  
  private TmdbSeries findSeries(int tmdbId) {
    var tmdbSeries = seriesClient.findById(tmdbId, CREDITS);
    LOGGER.infof("Found: %s", tmdbSeries);    
    return tmdbSeries;
  }
  
  private List<PersonCreateCommand> findPeople(TmdbSeries series, List<SeriesCreditCreateCommand> credits) {
    var people = credits.stream()
        .map(SeriesCreditCreateCommand::id)
        .distinct()
        .map(id -> personMapper.toPersonCreateCommand(personClient.findById(id)))
        .toList();
    LOGGER.infof("Found: %d people in %s", people.size(), series);
    return people;
  }
  
}
