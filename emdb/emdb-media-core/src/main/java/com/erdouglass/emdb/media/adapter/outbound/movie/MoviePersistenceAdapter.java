package com.erdouglass.emdb.media.adapter.outbound.movie;

import java.util.Optional;

import jakarta.data.exceptions.OptimisticLockingFailureException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.hibernate.StaleStateException;

import com.erdouglass.common.rest.StaleVersionException;
import com.erdouglass.emdb.media.SourceId;
import com.erdouglass.emdb.media.application.port.outbound.MovieRepository;
import com.erdouglass.emdb.media.domain.movie.Movie;
import com.erdouglass.emdb.media.domain.movie.MoviePublicId;
import com.erdouglass.emdb.media.domain.shared.Version;

/// Driven adapter: implements [MovieRepository] by translating shape via
/// [MovieMapper] and executing via the Jakarta Data [JakartaDataMovieRepository].
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
  JakartaDataMovieRepository repository;
  
  /// Persists a never-before-seen aggregate; returns it enriched with the
  /// database-assigned facts — [MoviePublicId] and the seed [Version].  
  @Override
  public Movie insert(Movie movie) {
    return mapper.toMovie(repository.insert(mapper.toMovieEntity(movie)));        
  }
  
  /// Version-guarded write: persists if and only if the stored version
  /// equals the aggregate's, returning the new snapshot with its bumped
  /// version.
  ///
  /// @throws StaleStateException when a version-guarded write finds the stored version differs
  /// from the caller's — a stale edit claim or a lost race with a
  /// concurrent writer.
  @Override
  public Movie update(Movie movie) {
    try {
      return mapper.toMovie(repository.update(mapper.toMovieEntity(movie)));
    } catch (OptimisticLockingFailureException e) {
      throw new StaleVersionException(
          "Version conflict updating movie %s".formatted(movie.publicId().orElse(null)), e);      
    }
  }
  
  @Override
  public Optional<Movie> findByPublicId(MoviePublicId publicId) {
    return repository.findByPublicId(publicId.value())
        .map(mapper::toMovie);
  }  
  
  @Override
  public Optional<Movie> findBySourceId(SourceId sourceId) {
    return repository.findBySourceId(sourceId.source().toString(), sourceId.id())
        .map(mapper::toMovie);
  }
}
