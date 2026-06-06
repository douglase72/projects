package com.erdouglass.emdb.media.series;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.erdouglass.emdb.media.command.SaveSeries;
import com.erdouglass.emdb.media.image.ImageService;
import com.erdouglass.emdb.media.logging.Log;

@ApplicationScoped
class SeriesService {

  @Inject
  ImageService imageService;

  @Inject
  SeriesMapper mapper;
  
  @Inject
  SeriesRepository seriesRepository;
  
  @Log
  @Transactional
  public Series save(final SaveSeries command) {
    Series series;
    var existing = seriesRepository.findByTmdbId(command.tmdbId()).orElse(null);
    if (existing == null) {
      var backdrop = imageService.save(command.backdrop());
      var poster = imageService.save(command.poster());
      series = seriesRepository.insert(mapper.toMovie(command, backdrop, poster));
    } else {
      var backdrop = imageService
          .update(existing.getTmdbBackdrop(), existing.getBackdrop(), command.backdrop());
      var poster = imageService
          .update(existing.getTmdbPoster(), existing.getPoster(), command.poster());
      var cmd = SaveSeries.builder(command)
          .backdrop(backdrop.image())
          .poster(poster.image())
          .build();
      mapper.merge(cmd, existing);
      series = seriesRepository.update(existing);
      backdrop.toDelete().ifPresent(imageService::delete);
      poster.toDelete().ifPresent(imageService::delete);
    }    
    return series;
  }
}
