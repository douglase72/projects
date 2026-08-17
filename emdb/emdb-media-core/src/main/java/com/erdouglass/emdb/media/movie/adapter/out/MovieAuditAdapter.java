package com.erdouglass.emdb.media.movie.adapter.out;

import java.time.Instant;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.movie.application.port.out.MovieAuditRepository;
import com.erdouglass.emdb.media.movie.domain.MovieFieldChange;
import com.erdouglass.emdb.media.movie.domain.MovieId;
import com.erdouglass.emdb.media.movie.domain.MoviePublicId;

/// Persists domain changes to the audit table.
///
/// Both movie identifiers are stored on every row: the surrogate id survives a
/// delete and keeps history attributable, while the public id keeps rows legible
/// to anyone reading the trail without joining back to the movie table.
///
/// Enlists in the caller's transaction, so audit rows commit with the change
/// they describe — a change can never be persisted without its trail, or the
/// reverse.
@ApplicationScoped
class MovieAuditAdapter implements MovieAuditRepository {
  
  @Inject
  JakartaDataMovieAuditRepository repository;
  
  /// Appends one row per field change, all stamped with the same instant.
  ///
  /// The timestamp is taken once and shared, so the rows produced by a single
  /// write group exactly. Reading history back and grouping by timestamp
  /// therefore reconstructs the original revisions.
  ///
  /// @param id the surrogate id of the changed title
  /// @param publicId the catalogue id of the changed title
  /// @param changes the field-level differences; an empty list writes nothing
  @Override
  public void append(MovieId id, MoviePublicId publicId, List<MovieFieldChange> changes) {
    var occurredAt = Instant.now();
    var rows = changes.stream()
        .map(change -> toMovieAuditEntity(id, publicId, change, occurredAt))
        .toList(); 
    repository.insertAll(rows);
  }
  
  /// Maps one domain change to one audit row.
  ///
  /// @param id the surrogate id of the changed title
  /// @param publicId the catalogue id of the changed title
  /// @param change the field-level difference to record
  /// @param occurredAt the instant shared by every row of this write
  /// @return the row to insert, without an id — the sequence supplies one
  private MovieAuditEntity toMovieAuditEntity(
      MovieId id, 
      MoviePublicId publicId, 
      MovieFieldChange change, 
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
