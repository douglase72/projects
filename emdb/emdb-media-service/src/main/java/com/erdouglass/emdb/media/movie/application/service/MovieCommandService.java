package com.erdouglass.emdb.media.movie.application.service;

import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.erdouglass.emdb.media.SaveMovieCommand;
import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.kernel.SaveResult;
import com.erdouglass.emdb.media.kernel.SaveResult.Status;
import com.erdouglass.emdb.media.kernel.TmdbId;
import com.erdouglass.emdb.media.kernel.UpdateResult;
import com.erdouglass.emdb.media.kernel.Version;
import com.erdouglass.emdb.media.movie.application.port.in.DeleteMovieUseCase;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieUseCase;
import com.erdouglass.emdb.media.movie.application.port.in.UpdateMovieCommand;
import com.erdouglass.emdb.media.movie.application.port.in.UpdateMovieUseCase;
import com.erdouglass.emdb.media.movie.application.port.out.MovieCommandRepository;
import com.erdouglass.emdb.media.movie.application.port.out.PersonStub;
import com.erdouglass.emdb.media.movie.application.port.out.ResolvePersonStub;
import com.erdouglass.emdb.media.movie.domain.event.DomainEvent;
import com.erdouglass.emdb.media.movie.domain.exception.MovieNotFoundException;
import com.erdouglass.emdb.media.movie.domain.model.Movie;
import com.erdouglass.emdb.media.person.domain.model.Name;

@ApplicationScoped
class MovieCommandService implements SaveMovieUseCase, UpdateMovieUseCase, DeleteMovieUseCase {
  
  @Inject
  Event<DomainEvent> emitter;
  
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
  public SaveResult save(SaveMovieCommand command) {
    return movies.findByTmdbId(TmdbId.of(command.tmdbId()))
        .map(existing -> update(existing, command))
        .orElseGet(() -> insert(command));
  }
  
  @Override
  @Transactional
  public UpdateResult update(UpdateMovieCommand command) {
    var existing = movies.findById(command.id())
        .orElseThrow(() -> new MovieNotFoundException(command.id().toString()));
    existing.checkVersion(Version.of(command.version()));
    existing.update(MovieDetailsMapper.toMovieDetails(command));
    var updated = movies.update(existing);
    existing.pullEvents().forEach(emitter::fire);
    return UpdateResult.of(updated.id(), updated.version(), UpdateResult.Status.UPDATED);
  }
  
  @Override
  @Transactional
  public void deleteById(PublicId id) {
    var existing = movies.findById(id)
        .orElseThrow(() -> new MovieNotFoundException(id.toString()));
    movies.deleteById(existing.id());
  }
  
  private SaveResult insert(SaveMovieCommand command) {
    var movie = Movie.create(TmdbId.of(command.tmdbId()), MovieDetailsMapper.toMovieDetails(command));
    var inserted = movies.insert(movie);
    people.resolve(Set.of(PersonStub.of(TmdbId.of(3), Name.of("Harrison Ford"))));
    movie.pullEvents().forEach(emitter::fire);
    return SaveResult.of(inserted.id(), Status.CREATED);
  }
  
  private SaveResult update(Movie existing, SaveMovieCommand command) {
    existing.update(MovieDetailsMapper.toMovieDetails(command));
    var updated = movies.update(existing);
    existing.pullEvents().forEach(emitter::fire);
    return SaveResult.of(updated.id(), Status.UPDATED);
  }
}
