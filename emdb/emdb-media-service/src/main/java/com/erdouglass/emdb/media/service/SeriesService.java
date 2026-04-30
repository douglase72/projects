package com.erdouglass.emdb.media.service;

import java.util.Objects;
import java.util.Optional;

import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.erdouglass.emdb.common.comand.SaveSeries;
import com.erdouglass.emdb.common.comand.UpdateSeries;
import com.erdouglass.emdb.common.query.SeriesQueryParameters;
import com.erdouglass.emdb.common.query.SeriesView;
import com.erdouglass.emdb.media.annotation.Logged;
import com.erdouglass.emdb.media.dto.SaveResult;
import com.erdouglass.emdb.media.dto.SaveResult.SaveStatus;
import com.erdouglass.emdb.media.entity.Series;
import com.erdouglass.emdb.media.mapper.SeriesMapper;
import com.erdouglass.emdb.media.repository.SeriesRepository;
import com.erdouglass.webservices.ResourceNotFoundException;

@ApplicationScoped
public class SeriesService {
  
  @Inject
  SeriesMapper mapper;

  @Inject
  SeriesRepository repository;
  
  @Transactional
  @Logged("Saved:")
  public SaveResult<Series> save(@NotNull @Valid SaveSeries command) {
    var status = SaveStatus.UNCHANGED;
    var existingSeries = repository.findByTmdbId(command.tmdbId()).orElse(null);
    if (existingSeries == null) {
      existingSeries = repository.insert(mapper.toSeries(command));
      status = SaveStatus.CREATED;
    } else if (!isEqual(command, existingSeries)) {
      mapper.merge(command, existingSeries);
      repository.update(existingSeries);
      status = SaveStatus.UPDATED;
    }
    return SaveResult.of(status, existingSeries);
  }
  
  @Transactional
  @Logged(value = "Found:", subject = "series")
  public Page<SeriesView> findAll(@NotNull @Valid SeriesQueryParameters parameters) {
    var pageRequest = PageRequest.ofPage(parameters.page(), parameters.size(), false);
    var results = repository.find(pageRequest);
    return results;
  }
  
  @Transactional
  @Logged("Found:")
  public Optional<Series> findById(@NotNull @Positive Long id, String append) {
    var series = repository.findById(id);
    return series;
  }
  
  /// Retrieves a single series by TMDB id.
  ///
  /// @param tmdbId the series TMDB id
  /// @param append optional comma-separated list of associations to include
  /// @return an [Optional] containing the series if found, or empty if not  
  @Transactional
  @Logged("Found:")
  public Optional<Series> findByTmdbId(@NotNull @Positive Integer tmdbId, String append) {
    return repository.findByTmdbId(tmdbId);
  }
  
  @Transactional
  @Logged("Updated:")
  public Series update(Long id, UpdateSeries command) {
    var existingSeries = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No series found with id: " + id));
    mapper.merge(command, existingSeries);
    return repository.update(existingSeries);
  }
  
  @Transactional
  @Logged("Deleted:")
  public Series delete(Long id) {
    var series = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No series found with id: " + id));
    repository.deleteById(id);
    return series;
  }
  
  private boolean isEqual(SaveSeries command, Series series) {
    if (command == null || series == null) return false;
    return Objects.equals(command.tmdbId(), series.getTmdbId())
        && Objects.equals(command.title(), series.getTitle())
        && Objects.equals(command.score(), series.getScore())
        && Objects.equals(command.status(), series.getStatus())
        && Objects.equals(command.type(), series.getType())
        && Objects.equals(command.backdrop() != null ? command.backdrop().name() : null, series.getBackdrop())
        && Objects.equals(command.poster() != null ? command.poster().name() : null, series.getPoster())
        && Objects.equals(command.homepage(), series.getHomepage())
        && Objects.equals(command.originalLanguage(), series.getOriginalLanguage())
        && Objects.equals(command.overview(), series.getOverview());
  }
}
