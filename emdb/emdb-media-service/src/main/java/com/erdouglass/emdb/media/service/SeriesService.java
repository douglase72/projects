package com.erdouglass.emdb.media.service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.comand.SaveSeries;
import com.erdouglass.emdb.media.entity.Series;
import com.erdouglass.emdb.media.mapper.SeriesMapper;
import com.erdouglass.emdb.media.repository.SeriesRepository;
import com.erdouglass.emdb.scraper.service.TmdbSeriesScraper;

@ApplicationScoped
public class SeriesService extends MediaService {
  private static final Logger LOGGER = Logger.getLogger(SeriesService.class);
  
  @Inject
  SeriesMapper mapper;
  
  @Inject
  TmdbSeriesScraper scraper;
  
  @Inject
  SeriesRepository repository;
  
  @Override
  public void ingest(@NotNull @Positive Integer tmdbId, @NotNull UUID jobId) {
    var existingSeries = findByTmdbId(tmdbId);
    var command = existingSeries
        .map(mapper::toSaveSeries)
        .orElseGet(() -> SaveSeries.builder().tmdbId(tmdbId).build());
    var saveCommand = scraper.scrape(command, jobId);
    
    var series = save(saveCommand);
    LOGGER.infof("Saved: %s", series);
    existingSeries.ifPresent(s -> {
      if (!Objects.equals(s.tmdbBackdrop().orElse(null), series.tmdbBackdrop().orElse(null))) {
        s.backdrop().ifPresent(imageService::delete);
      }
      if (!Objects.equals(s.tmdbPoster().orElse(null), series.tmdbPoster().orElse(null))) {
        s.poster().ifPresent(imageService::delete);
      }      
    });
  }  
  
  @Transactional
  public Series save(SaveSeries command) {
    var series = mapper.toSeries(command);
    repository.findByTmdbId(series.tmdbId()).ifPresent(s -> series.id(s.id()));
    var savedSeries = repository.save(series);
    return savedSeries;     
  }
  
  @Transactional
  public Optional<Series> findById(@NotNull @Positive Long id, String append) {
    return repository.findById(id);
  }
  
  @Transactional
  Optional<Series> findByTmdbId(@NotNull @Positive Integer id) {
    return repository.findByTmdbId(id);
  }

}
