package com.erdouglass.emdb.media.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.erdouglass.common.graphql.ResourceNotFoundException;
import com.erdouglass.emdb.media.application.port.inbound.series.SeriesCommandService;
import com.erdouglass.emdb.media.application.port.inbound.series.SeriesQueryService;
import com.erdouglass.emdb.media.application.port.inbound.series.SeriesView;
import com.erdouglass.emdb.media.application.port.inbound.series.SeriesView.SeriesCredits;
import com.erdouglass.emdb.media.application.port.inbound.series.UpdateSeries;
import com.erdouglass.emdb.media.domain.series.SeriesRepository;

@ApplicationScoped
class SeriesFacade implements SeriesCommandService, SeriesQueryService {
  
  @Inject
  SeriesMapper mapper;
  
  @Inject
  SeriesRepository repository;

  @Override
  public SeriesView findById(Long id) {
    return repository.findById(id)
        .map(mapper::toSeriesView)
        .orElseThrow(() -> new ResourceNotFoundException("No series found with id: " + id));   
  }

  @Override
  public SeriesView update(UpdateSeries command) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void deleteById(Long id) {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public SeriesCredits findCreditsBySeriesId(Long id) {
    throw new UnsupportedOperationException();
  }
}
