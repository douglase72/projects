package com.erdouglass.emdb.media.adapter.outbound.movie;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.application.port.outbound.MovieRepository;
import com.erdouglass.emdb.media.domain.movie.Movie;
import com.erdouglass.emdb.media.domain.shared.PublicId;
import com.erdouglass.emdb.media.domain.shared.SourceId;

/// Driven adapter: implements [MovieRepository] by translating shape via
/// [MovieMapper] and executing via the Jakarta Data [MovieCrudRepository].
///
/// Every write maps back the *returned* entity, not the input — that is how
/// database-assigned facts (public id on insert, bumped version on update)
/// reach the caller. This is also the seam where spec exceptions become
/// port vocabulary, so `jakarta.data` never leaks past [MovieRepository].
@ApplicationScoped
class MoviePersistenceAdapter implements MovieRepository {
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  MovieCrudRepository repository;
  
  /// Persists a never-before-seen aggregate; returns it enriched with the
  /// database-assigned facts — [PublicId] and the seed [Version].  
  @Override
  public Movie insert(Movie movie) {
    return mapper.toMovie(repository.insert(mapper.toMovieEntity(movie)));        
  }
  
  /// Version-guarded write: persists if and only if the stored version
  /// equals the aggregate's, returning the new snapshot with its bumped
  /// version.
  ///
  /// @throws StaleVersionException when the guard fails — a stale edit
  ///         claim, or a concurrent writer in the in-flight race
  @Override
  public Movie update(Movie movie) {
    return mapper.toMovie(repository.update(mapper.toMovieEntity(movie)));
  }
  
  @Override
  public Optional<Movie> findByPublicId(PublicId publicId) {
    return repository.findByPublicId(publicId.value())
        .map(mapper::toMovie);
  }  
  
  @Override
  public Optional<Movie> findBySourceId(SourceId sourceId) {
    return repository.findBySourceId(sourceId.source().toString(), sourceId.id())
        .map(mapper::toMovie);
  }
}
