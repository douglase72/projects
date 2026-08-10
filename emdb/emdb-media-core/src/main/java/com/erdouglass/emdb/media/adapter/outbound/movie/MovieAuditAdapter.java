package com.erdouglass.emdb.media.adapter.outbound.movie;

import java.time.Instant;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.application.port.outbound.movie.MovieAuditRepository;
import com.erdouglass.emdb.media.domain.movie.FieldChange;
import com.erdouglass.emdb.media.domain.movie.MovieId;
import com.erdouglass.emdb.media.domain.movie.MoviePublicId;

@ApplicationScoped
class MovieAuditAdapter implements MovieAuditRepository {
  
  @Inject
  JakartaDataMovieAuditRepository repository;
  
  @Override
  public void append(MovieId id, MoviePublicId publicId, List<FieldChange> changes) {
    var occurredAt = Instant.now();
    var rows = changes.stream()
        .map(change -> toMovieAuditEntity(id, publicId, change, occurredAt))
        .toList(); 
    repository.insertAll(rows);
  }
  
  private MovieAuditEntity toMovieAuditEntity(
      MovieId id, 
      MoviePublicId publicId, 
      FieldChange change, 
      Instant occurredAt) {
    var entity = new MovieAuditEntity();
    entity.setOccurredAt(occurredAt);
    entity.setMovieSurrogateId(id.value());
    entity.setMoviePublicId(publicId.value());
    entity.setField(change.field());
    entity.setOperation(change.operation());
    entity.setOldValue(change.oldValue());
    entity.setNewValue(change.newValue());
    return entity;
  }
}
