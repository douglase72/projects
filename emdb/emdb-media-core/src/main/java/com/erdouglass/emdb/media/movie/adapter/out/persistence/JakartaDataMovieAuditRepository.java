package com.erdouglass.emdb.media.movie.adapter.out.persistence;

import java.util.List;

import jakarta.data.repository.Insert;
import jakarta.data.repository.Repository;

/// Jakarta Data repository for the append-only audit table.
///
/// Insert-only by design: there is no update, delete or find here, because an
/// audit trail that can be rewritten is not evidence. Reading history is a
/// reporting concern and is not served from this interface.
@Repository(dataStore = "media")
interface JakartaDataMovieAuditRepository {

  /// Appends a batch of audit rows.
  ///
  /// Batched rather than row-by-row because one logical write commonly produces
  /// several rows — a creation emits one per populated field — and because the
  /// rows of a single change should land together or not at all. Runs in the
  /// caller's transaction.
  ///
  /// @param entries the rows to append; an empty list is a no-op
  @Insert
  void insertAll(List<MovieAuditEntity> entries);
}
