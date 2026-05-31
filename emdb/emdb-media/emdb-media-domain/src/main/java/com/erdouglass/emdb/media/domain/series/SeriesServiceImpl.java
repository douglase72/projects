package com.erdouglass.emdb.media.domain.series;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.domain.SeriesService;
import com.erdouglass.emdb.media.domain.internal.ImageService;
import com.erdouglass.emdb.media.series.SaveSeries;
import com.erdouglass.emdb.media.series.SeriesResponse;

@ApplicationScoped
class SeriesServiceImpl implements SeriesService {
  private static final Logger LOGGER = Logger.getLogger(SeriesServiceImpl.class);
  
  @Inject
  ImageService imageService;

  @Inject
  SeriesMapper mapper;
  
  @Inject
  SeriesRepository seriesRepository;
  
  @Override
  public SeriesResponse save(final SaveSeries command) {
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
    LOGGER.infof("Saved: %s", series);
    return mapper.toSeriesResponse(series);
  }
}
