package com.erdouglass.emdb.media.movie.application.service;

import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.SaveMovieCommand;
import com.erdouglass.emdb.media.kernel.TmdbId;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieUseCase;
import com.erdouglass.emdb.media.movie.application.port.out.MovieCommandRepository;
import com.erdouglass.emdb.media.movie.application.port.out.PersonStub;
import com.erdouglass.emdb.media.movie.application.port.out.ResolvePersonStub;
import com.erdouglass.emdb.media.movie.domain.model.Movie;
import com.erdouglass.emdb.media.person.domain.model.Name;

@ApplicationScoped
class MovieCommandService implements SaveMovieUseCase {
  private static final Logger LOGGER = Logger.getLogger(MovieCommandService.class);
  
  @Inject
  MovieCommandRepository movies;
  
  @Inject
  ResolvePersonStub people;
  
  /// Save the movie described by the command to the database.
  /// 
  /// This method is idempotent with respect to the movies TMDB id. If a movie
  /// with a matching TMDB id does not already exist, one will be created. 
  /// Otherwise, the movie details are updated making retries safe.
  @Override
  @Transactional
  public void save(SaveMovieCommand command) {
    var details = MovieDetailsMapper.toMovieDetails(command);
    var movie = Movie.create(TmdbId.of(command.tmdbId()), details);
    var inserted = movies.insert(movie);
    people.resolve(Set.of(PersonStub.of(TmdbId.of(3), Name.of("Harrison Ford"))));
    LOGGER.infof("movie: %s", inserted);
  }
}
