package com.erdouglass.emdb.media.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

import com.erdouglass.common.graphql.ResourceNotFoundException;
import com.erdouglass.emdb.media.SaveResult;
import com.erdouglass.emdb.media.SaveResult.Status;
import com.erdouglass.emdb.media.SaveSeries;
import com.erdouglass.emdb.media.SaveSeriesService;
import com.erdouglass.emdb.media.application.port.inbound.series.DeleteSeriesUseCase;
import com.erdouglass.emdb.media.application.port.inbound.series.QuerySeriesUseCase;
import com.erdouglass.emdb.media.application.port.inbound.series.SeriesView;
import com.erdouglass.emdb.media.application.port.inbound.series.SeriesView.SeriesCredits;
import com.erdouglass.emdb.media.application.port.inbound.series.UpdateSeries;
import com.erdouglass.emdb.media.application.port.inbound.series.UpdateSeriesUseCase;
import com.erdouglass.emdb.media.domain.series.Series;
import com.erdouglass.emdb.media.domain.series.SeriesRepository;

@ApplicationScoped
class SeriesService implements SaveSeriesService, UpdateSeriesUseCase, DeleteSeriesUseCase,
    QuerySeriesUseCase {
  private static final Logger LOGGER = Logger.getLogger(SeriesService.class);
  
  @Inject
  ImageService imageService;
  
  @Inject
  SeriesMapper mapper;
  
  @Inject
  SeriesRepository repository;
  
  @Override
  @Transactional
  public SaveResult save(SaveSeries command) {
    SaveResult result;
    Series series;
    var existing = repository.findByExternalId(command.externalId()).orElse(null); 
    if (existing == null) {
      imageService.save(command.backdrop());
      imageService.save(command.poster());
      series = repository.insert(mapper.toSeries(command));
      result = new SaveResult(series.getId(), Status.CREATED);
    } else {
      var backdrop = imageService.update(existing.getBackdrop(), command.backdrop());
      var poster = imageService.update(existing.getPoster(), command.poster());
      var cmd = SaveSeries.builder(command)
          .backdrop(backdrop.image())
          .poster(poster.image())
          .build();
      mapper.merge(cmd, existing);
      series = repository.update(existing);      
      backdrop.toDelete().ifPresent(imageService::delete);
      poster.toDelete().ifPresent(imageService::delete); 
      result = new SaveResult(series.getId(), Status.UPDATED);
    }
    LOGGER.infof("Saved: %s", series);
    return result;
  }

  @Override
  public SeriesView findById(Long id) {
    return repository.findById(id)
        .map(mapper::toSeriesView)
        .orElseThrow(() -> new ResourceNotFoundException("No series found with id: " + id));   
  }
  
  @Override
  public SeriesView update(Long id, UpdateSeries command) {
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
