package com.erdouglass.emdb.scraper.producer;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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

import com.erdouglass.emdb.common.command.PersonCreateCommand;
import com.erdouglass.emdb.common.command.SeriesCreateCommand;
import com.erdouglass.emdb.common.command.SeriesCreditCreateCommand;
import com.erdouglass.emdb.scraper.client.TmdbSeriesClient;
import com.erdouglass.emdb.scraper.dto.TmdbSeries;
import com.erdouglass.emdb.scraper.mapper.TmdbSeriesCreditMapper;

import io.quarkus.scheduler.Scheduled;

@ApplicationScoped
public class TmdbSeriesScraper extends TmdbScraper<TmdbSeries> {
  private static final Logger LOGGER = Logger.getLogger(TmdbMovieScraper.class);
  private static final String CREDITS = "aggregate_credits";
  
  @Inject
  TmdbSeriesCreditMapper creditMapper;
  
  @Inject
  @Channel("series")
  Emitter<SeriesCreateCommand> emitter;
  
  @Inject
  @RestClient
  TmdbSeriesClient seriesClient;
  
  /// Periodically ingests and synchronizes TV series data from TMDB.
  ///
  /// Cron Examples:
  /// <ul>
  /// <li>{@code @Scheduled(cron = "0 0 0 * * ?")} - Run every day at midnight UTC (17:00:00 MST)</li>
  /// <li>{@code @Scheduled(cron = "0 10 0 * * ?")} - Run every day at 00:10:00 UTC (17:10:00 MST)</li>
  /// <li>{@code @Scheduled(every = "75s", delayed = "75s")} - Run every 75 seconds</li>
  /// </ul>
  @Scheduled(cron = "0 0 0 * * ?")
  public void cron() {
    // Get the TMDB ids of all TV series that have changed in the last 24 hours.
    var endDate = LocalDate.now();
    var startDate = endDate.minusDays(1); 
    var tmdbIds = findTmdbChanges(startDate, endDate);
    
    // Get the EMDB ids of all TV series
    var emdbIds = Map.of(71912, 12L, 456, 1L);
    
    // Ingest or synchronize each changed TV series from TMDB with EMDB.
    for (var tmdbId : tmdbIds) {
      var emdbId = emdbIds.get(tmdbId);
      if (emdbId == null) {
        ingest(tmdbId);
      } else {
        synchronize(emdbId, tmdbId);
      }
    }   
  }

  /// Ingest a TV series from TMDB.
  ///
  /// This is a potentially long running process
  @Override
  public void ingest(@NotNull @Positive Integer tmdbId) {
    LOGGER.infof("Ingesting TMDB series id: %d", tmdbId);
    try {
      var tmdbSeries = findSeries(tmdbId);
      var credits = findCredits(tmdbSeries);
      var people = findPeople(tmdbSeries, credits);
      var message = createMessage(tmdbSeries, credits, people);
      emitter.send(message);
      LOGGER.infof("Sent: %s", message);
    } catch (Exception e) {
      LOGGER.error(e);
    }
  }
  
  /// Synchronize a TV series in EMDB with the one from TMDB.
  ///
  /// This is a potentially long running process
  @Override
  public void synchronize(@NotNull @Positive Long emdbId, @NotNull @Positive Integer tmdbId) {
    LOGGER.infof("Synchronizing EMDB series id: %d with TMDB series id: %d", emdbId, tmdbId);
    try {
      Thread.sleep(3000);
    } catch (Exception e) {
      LOGGER.error(e);
    }
  }
  
  private SeriesCreateCommand createMessage(
      TmdbSeries series, List<SeriesCreditCreateCommand> credits, List<PersonCreateCommand> people) {
    return SeriesCreateCommand.builder()
        .tmdbId(series.id())
        .name(series.name())
        .score(series.vote_average())
        .status(series.status())
        .type(series.type())
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
  
  private List<PersonCreateCommand> findPeople(TmdbSeries series, List<SeriesCreditCreateCommand> credits) {
    var people = credits.stream()
        .map(SeriesCreditCreateCommand::id)
        .distinct()
        .map(id -> personMapper.toPersonCreateCommand(personClient.findById(id)))
        .toList();
    LOGGER.infof("Found: %d people in %s", people.size(), series);
    return people;
  }
  
  private TmdbSeries findSeries(int tmdbId) {
    var tmdbSeries = seriesClient.findById(tmdbId, CREDITS);
    Set<ConstraintViolation<TmdbSeries>> violations = validator.validate(tmdbSeries);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException("Invalid TMDB series " + tmdbId, violations);
    }
    LOGGER.infof("Found: %s", tmdbSeries);    
    return tmdbSeries;
  }
  
  private List<Integer> findTmdbChanges(LocalDate startDate, LocalDate endDate) {
    LOGGER.infof("Looking for TMDB series changes between %s - %s", startDate, endDate);
    return List.of(66732, 456, 200875);
  }
  
}
