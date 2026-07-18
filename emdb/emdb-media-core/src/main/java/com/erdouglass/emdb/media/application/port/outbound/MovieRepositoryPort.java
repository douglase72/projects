package com.erdouglass.emdb.media.application.port.outbound;

import com.erdouglass.emdb.media.domain.movie.Movie;

/// Outbound (driven) port for persisting the [Movie] aggregate.
///
/// This interface is the dependency-inversion seam of the architecture: the
/// application layer *owns* it, the persistence adapter *implements* it, and
/// therefore the dependency arrow points inward even though data flows out.
/// It speaks exclusively domain language — aggregates and value objects; no
/// entity, SQL, or Jakarta Data type may appear in its signatures.
///
/// Contract: [save] has upsert semantics keyed on [SourceId] — the movie's
/// external identity — making ingestion idempotent. The returned [Movie] is
/// the authoritative post-save state, including the database-generated
/// [PublicId] and whether the call created or updated
/// (a fact only the adapter can know).
public interface MovieRepositoryPort {

  SaveStatus save(Movie movie);
}
