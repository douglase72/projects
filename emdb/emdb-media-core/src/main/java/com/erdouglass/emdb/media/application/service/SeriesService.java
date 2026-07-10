package com.erdouglass.emdb.media.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.erdouglass.common.graphql.ResourceNotFoundException;
import com.erdouglass.emdb.media.application.port.inbound.SeriesCommandService;
import com.erdouglass.emdb.media.application.port.inbound.SeriesQueryService;
import com.erdouglass.emdb.media.application.port.inbound.SeriesView;
import com.erdouglass.emdb.media.application.port.inbound.UpdateSeries;
import com.erdouglass.emdb.media.domain.series.SeriesRepository;

@ApplicationScoped
class SeriesService implements SeriesCommandService, SeriesQueryService {
  
  @Inject
  SeriesMapper mapper;
  
  @Inject
  SeriesRepository repository;

  @Override
  public SeriesView findById(@NotNull @Positive Long id) {
    return repository.findById(id)
        .map(mapper::toSeriesView)
        .orElseThrow(() -> new ResourceNotFoundException("No series found with id: " + id));   
  }

  @Override
  public SeriesView update(@NotNull @Valid UpdateSeries command) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void deleteById(@NotNull @Positive Long id) {
    throw new UnsupportedOperationException();
  }
}
