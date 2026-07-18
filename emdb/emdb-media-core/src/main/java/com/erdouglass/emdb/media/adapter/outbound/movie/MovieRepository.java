package com.erdouglass.emdb.media.adapter.outbound.movie;

import java.util.Optional;
import java.util.UUID;

import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Update;

/// Jakarta Data repository over the movie table — an internal *tool* of the
/// persistence adapter, not the DDD repository. That role belongs to
/// [MovieRepositoryPort]; this interface is typed in [MovieEntity] and spec
/// annotations, which is exactly why it must stay package-private and below
/// the port.
///
/// Backed by Hibernate's stateless session: operations execute immediately,
/// nothing is managed, nothing cascades, and updates are explicit — the
/// adapter above compensates accordingly.
@Repository(dataStore = "media")
interface MovieRepository {

  @Insert
  MovieEntity insert(MovieEntity entity);
  
  @Update
  MovieEntity update(MovieEntity entity);
  
  @Find
  Optional<MovieEntity> findById(UUID id);
  
  @Find
  Optional<MovieEntity> findBySourceId(String source, String sourceId);
}
