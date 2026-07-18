package com.erdouglass.emdb.media.adapter.outbound.movie;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.SaveResult.Status;
import com.erdouglass.emdb.media.application.port.outbound.MovieRepositoryPort;
import com.erdouglass.emdb.media.application.port.outbound.SaveStatus;
import com.erdouglass.emdb.media.domain.movie.Movie;
import com.erdouglass.emdb.media.domain.movie.PublicId;
import com.erdouglass.emdb.media.domain.shared.SourceId;

/// Driven adapter implementing [MovieRepositoryPort] over Jakarta Data.
///
/// The translation layer where domain and persistence meet: aggregates cross
/// downward into [MovieEntity] rows, rows cross upward through the mapper —
/// and every infrastructure concern (stateless-session semantics, generated
/// [PublicId] retrieval, upsert mechanics) is absorbed here so the port's
/// contract stays clean.
///
/// Upsert strategy: look up by external identity, then insert or update.
/// The check-then-act pair is subject to a race under concurrent ingestion
/// of the same [SourceId]; the `uq_movie_source` constraint remains the
/// authoritative guard, so a losing racer surfaces a constraint violation
/// rather than a duplicate row.
@ApplicationScoped
class MoviePersistenceAdapter implements MovieRepositoryPort {
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  MovieRepository repository;

  @Override
  public SaveStatus save(Movie movie) {
    Status status = Status.CREATED;
    MovieEntity saved;
    var sid = movie.sourceId();
    var existing = repository.findBySourceId(sid.source().toString(), sid.id()).orElse(null);
    if (existing == null) {
      repository.insert(mapper.toMovieEntity(movie));
      saved = repository.findById(movie.id().value()).orElseThrow();
    } else {
      merge(movie, existing);
      saved = repository.update(existing);
      status = Status.UPDATED;
    }
    return new SaveStatus(mapper.toMovie(saved), status);
  }
  
  public void merge(Movie movie, MovieEntity entity) {
    entity.setReleaseDate(movie.releaseDate().value());
    entity.setTitle(movie.title().value());
  }
}
