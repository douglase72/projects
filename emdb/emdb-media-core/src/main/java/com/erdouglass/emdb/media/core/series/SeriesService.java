package com.erdouglass.emdb.media.core.series;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.erdouglass.emdb.media.core.ImageService;
import com.erdouglass.emdb.media.core.Log;
import com.erdouglass.emdb.media.series.SaveSeries;
import com.erdouglass.emdb.media.series.SeriesCommandService;
import com.erdouglass.emdb.media.series.SeriesDto;
import com.erdouglass.emdb.media.series.UpdateSeries;

@ApplicationScoped
class SeriesService implements SeriesCommandService {
  
  @Inject
  ImageService imageService;
  
  @Inject
  SeriesMapper mapper;
  
  @Inject
  SeriesRepository repository;

  @Override
  @Log("Saved:")
  @Transactional
  public SeriesDto save(SaveSeries command) {
    Series series;
    var existing = repository.findByTmdbId(command.tmdbId()).orElse(null);
    if (existing == null) {
      imageService.save(command.backdrop());
      imageService.save(command.poster());
      series = repository.insert(mapper.toSeries(command));
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
    }
    return mapper.toSeriesDto(series);
  }

  @Override
  public SeriesDto update(UpdateSeries command) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void delete(Long id) {
    throw new UnsupportedOperationException();
  }
}
