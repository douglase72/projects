package com.erdouglass.emdb.media.adapter.outbound.movie;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.SaveResult.Status;
import com.erdouglass.emdb.media.application.port.outbound.MovieRepositoryPort;
import com.erdouglass.emdb.media.domain.movie.Movie;

@ApplicationScoped
class MoviePersistenceAdapter implements MovieRepositoryPort {
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  MovieRepository repository;

  @Override
  public Movie save(Movie movie) {
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
    return mapper.toMovie(saved, status);
  }
  
  public void merge(Movie movie, MovieEntity entity) {
    entity.setReleaseDate(movie.releaseDate().value());
    entity.setTitle(movie.title().value());
  }
}
