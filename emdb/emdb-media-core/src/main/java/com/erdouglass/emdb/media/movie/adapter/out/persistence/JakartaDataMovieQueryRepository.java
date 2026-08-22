package com.erdouglass.emdb.media.movie.adapter.out.persistence;

import java.util.Optional;

import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import com.erdouglass.emdb.media.movie.application.port.out.MovieView;

/// Jakarta Data repository for the read side of the movie table.
///
/// Projects straight into the client-facing view rather than loading entities,
/// so a read touches only the columns it returns and never materialises an
/// aggregate. Internal columns — the surrogate id, the TMDB id, the lock flag —
/// are absent from the projection by design.
@Repository(dataStore = "media")
interface JakartaDataMovieQueryRepository {

  /// Projects a single title by primary key.
  ///
  /// The selected columns line up positionally with the raw-value constructor of
  /// the view, which converts the numeric key into the prefixed catalogue id.
  /// Reordering the select list without reordering that constructor will bind
  /// the wrong columns.
  ///
  /// @param id the numeric primary key
  /// @return the projected title, or empty if none carries that key
  @Query("""
    select m.id, m.version, m.title, m.releaseDate, m.score, m.originalLanguage, m.overview
    from MovieEntity m
    where m.id = :id          
          """)
  Optional<MovieView> findById(Long id);
}
