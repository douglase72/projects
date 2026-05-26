package com.erdouglass.emdb.media.domain.movie;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.api.command.SaveMovie;
import com.erdouglass.emdb.media.api.query.MovieResponse;
import com.erdouglass.emdb.media.domain.ImageService;
import com.erdouglass.emdb.media.domain.MovieService;

@ApplicationScoped
class MovieServiceImpl implements MovieService {
  private static final Logger LOGGER = Logger.getLogger(MovieServiceImpl.class);
  
  @Inject
  ImageService imageService;
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  MovieRepository repository;

  @Override
  @Transactional
  public MovieResponse save(final SaveMovie command) {
    Movie savedMovie;
    var existing = repository.findByTmdbId(command.tmdbId()).orElse(null);
    if (existing == null) {
      var backdrop = imageService.save(command.backdrop());
      var poster = imageService.save(command.poster());
      savedMovie = repository.insert(mapper.toMovie(command, backdrop, poster));
    } else {
      var backdrop = imageService
          .update(existing.getTmdbBackdrop(), existing.getBackdrop(), command.backdrop());
      var poster = imageService
          .update(existing.getTmdbPoster(), existing.getPoster(), command.poster());
      var cmd = SaveMovie.builder(command)
          .backdrop(backdrop.image())
          .poster(poster.image())
          .build();
      mapper.merge(cmd, existing);
      savedMovie = repository.update(existing);
      backdrop.toDelete().ifPresent(imageService::delete);
      poster.toDelete().ifPresent(imageService::delete);
    }
    LOGGER.infof("Saved: %s", savedMovie);
    return mapper.toMovieResponse(savedMovie);
  }
}
