package com.erdouglass.emdb.media.core.movie;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.erdouglass.emdb.media.core.ImageService;
import com.erdouglass.emdb.media.core.Log;
import com.erdouglass.emdb.media.movie.MovieDto;
import com.erdouglass.emdb.media.movie.MovieCommandService;
import com.erdouglass.emdb.media.movie.SaveMovie;
import com.erdouglass.emdb.media.movie.UpdateMovie;

@ApplicationScoped
class MovieService implements MovieCommandService {
  
  @Inject
  ImageService imageService;
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  MovieRepository repository;
  
  /// Upsert the [Movie] in the given command.
  /// 
  /// This method is idempotent, creating a movie if one does not already exist 
  /// by the given TMDB ID or updating the existing one.
  @Override
  @Log("Saved:")
  @Transactional
  public MovieDto save(SaveMovie command) {
    Movie movie;
    var existing = repository.findByTmdbId(command.tmdbId()).orElse(null);
    if (existing == null) {
      imageService.save(command.backdrop());
      imageService.save(command.poster());
      movie = repository.insert(mapper.toMovie(command));
    } else {
      var backdrop = imageService.update(existing.getBackdrop(), command.backdrop());
      var poster = imageService.update(existing.getPoster(), command.poster());
      var cmd = SaveMovie.builder(command)
          .backdrop(backdrop.image())
          .poster(poster.image())
          .build();
      mapper.merge(cmd, existing);
      movie = repository.update(existing);      
      backdrop.toDelete().ifPresent(imageService::delete);
      poster.toDelete().ifPresent(imageService::delete);
    }
    return mapper.toMovieDto(movie);
  }

  @Override
  @Transactional
  public MovieDto update(UpdateMovie command) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Transactional
  public void delete(Long id) {
    throw new UnsupportedOperationException();
  }
}
