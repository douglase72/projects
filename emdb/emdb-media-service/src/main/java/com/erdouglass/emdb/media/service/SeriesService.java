package com.erdouglass.emdb.media.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.command.SeriesUpdateCommand;
import com.erdouglass.emdb.media.entity.Series;
import com.erdouglass.emdb.media.repository.SeriesRepository;
import com.erdouglass.webservices.ResourceNotFoundException;

@ApplicationScoped
public class SeriesService {
  private static final Logger LOGGER = Logger.getLogger(SeriesService.class);
  
  @Inject
  SeriesRepository repository;
  
  @Transactional
  public Series create(@NotNull @Valid Series series) {
    var newSeries = repository.insert(series);
    LOGGER.infof("Created: %s", newSeries);
    return newSeries;
  }
  
  @Transactional
  public Series findById(@NotNull @Positive Long id) {
    var series = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No series found with id: " + id));
    LOGGER.infof("Found: %s", series);
    return series;
  }
  
  @Transactional
  public Series findByTmdbId(@NotNull @Positive Integer tmdbId) {
    var series = repository.findByTmdbId(tmdbId)
        .orElseThrow(() -> new ResourceNotFoundException("No series found with tmdbId: " + tmdbId));
    LOGGER.infof("Found: %s", series);
    return series;
  }
  
  @Transactional
  public Series update(@NotNull @Positive Long id, @NotNull @Valid SeriesUpdateCommand command) {
    var series = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No series found with id: " + id));
    command.name().ifPresent(series::name);
    command.score().ifPresent(series::score);
    command.status().ifPresent(series::status);
    command.type().ifPresent(series::type);
    command.homepage().ifPresent(series::homepage);
    command.originalLanguage().ifPresent(series::originalLanguage);
    command.backdrop().ifPresent(series::backdrop);
    command.poster().ifPresent(series::poster);
    command.tagline().ifPresent(series::tagline);
    command.overview().ifPresent(series::overview);
    var updatedSeries = repository.update(series);
    LOGGER.infof("Updated: %s", updatedSeries);
    return updatedSeries;
  }
  
  @Transactional
  public void delete(@NotNull @Positive Long id) {
    var series = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No series found with id: " + id));
    repository.deleteById(id);
    LOGGER.infof("Deleted: %s", series);
  }
  
}
