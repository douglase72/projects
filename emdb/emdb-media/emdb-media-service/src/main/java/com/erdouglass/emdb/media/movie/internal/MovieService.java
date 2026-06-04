package com.erdouglass.emdb.media.movie.internal;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.erdouglass.emdb.media.command.SaveMovie;
import com.erdouglass.emdb.media.internal.ImageService;
import com.erdouglass.emdb.media.internal.Log;
import com.erdouglass.emdb.media.internal.PersonResolver;

@ApplicationScoped
class MovieService {
  
  @Inject
  ImageService imageService;
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  MovieRepository movieRepository;
  
  @Inject
  PersonResolver resolver;

  @Log
  @Transactional
  public Movie save(final SaveMovie command) {
    Movie movie;
    var existing = movieRepository.findByTmdbId(command.tmdbId()).orElse(null);
    if (existing == null) {
      var backdrop = imageService.save(command.backdrop());
      var poster = imageService.save(command.poster());
      movie = movieRepository.insert(mapper.toMovie(command, backdrop, poster));
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
      movie = movieRepository.update(existing);
      backdrop.toDelete().ifPresent(imageService::delete);
      poster.toDelete().ifPresent(imageService::delete);
    }
    return movie;
  }
}
