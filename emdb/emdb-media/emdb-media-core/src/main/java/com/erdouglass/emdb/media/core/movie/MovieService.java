package com.erdouglass.emdb.media.core.movie;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.command.MovieCommandService;
import com.erdouglass.emdb.media.command.SaveMovie;
import com.erdouglass.emdb.media.command.UpdateMovie;
import com.erdouglass.emdb.media.core.ImageService;
import com.erdouglass.emdb.media.query.MovieDto;
import com.erdouglass.emdb.media.query.MovieQueryService;

@ApplicationScoped
class MovieService implements MovieCommandService, MovieQueryService {
  private static final Logger LOGGER = Logger.getLogger(MovieService.class);
  
  @Inject
  ImageService imageService;
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  MovieRepository movieRepository;
  
  /// Upsert the [Movie] in the given command.
  /// 
  /// This method is idempotent, creating a movie if one does not already exist 
  /// by the given TMDB ID or updating the existing one.
  @Override
  @Transactional
  public MovieDto save(SaveMovie command) {
    Movie movie;
    var existing = movieRepository.findByTmdbId(command.tmdbId()).orElse(null);
    if (existing == null) {
      imageService.save(command.backdrop());
      imageService.save(command.poster());
      movie = movieRepository.insert(mapper.toMovie(command));
    } else {
      var backdrop = imageService.update(existing.getBackdrop(), command.backdrop());
      var poster = imageService.update(existing.getPoster(), command.poster());
      var cmd = SaveMovie.builder(command)
          .backdrop(backdrop.image())
          .poster(poster.image())
          .build();
      mapper.merge(cmd, existing);
      movie = movieRepository.update(existing);      
      backdrop.toDelete().ifPresent(imageService::delete);
      poster.toDelete().ifPresent(imageService::delete);
    }
    LOGGER.infof("Saved: %s", movie);
    return mapper.toMovieDto(movie);
  }
  
  @Override
  @Transactional
  public MovieDto findById(Long id) {
    throw new UnsupportedOperationException();
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
