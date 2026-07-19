package com.erdouglass.emdb.media.application.port.outbound;

import java.util.Optional;

import com.erdouglass.emdb.media.domain.movie.Movie;

/// Outbound (driven) port for persisting the [Movie] aggregate.
///
/// This interface is the dependency-inversion seam of the architecture: the
/// application layer *owns* it, the persistence adapter *implements* it, and
/// therefore the dependency arrow points inward even though data flows out.
/// It speaks exclusively domain language — aggregates and value objects; no
/// entity, SQL, or Jakarta Data type may appear in its signatures.
public interface MovieRepository {

  Movie insert(Movie movie);
  
  Movie update(Movie movie);
  
  Optional<Movie> findBySourceId(String source, String sourceId);
}
