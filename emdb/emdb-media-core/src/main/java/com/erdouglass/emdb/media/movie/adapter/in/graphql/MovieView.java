package com.erdouglass.emdb.media.movie.adapter.in.graphql;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.eclipse.microprofile.graphql.NonNull;

import com.erdouglass.emdb.media.movie.domain.MoviePublicId;

/// The read model of a title, as clients see it.
///
/// Built directly by the query repository rather than mapped from the aggregate,
/// so reads never load or rehydrate a [Movie]. That is the point of the split:
/// the projection can select exactly the columns it needs and can change shape
/// without touching the domain.
///
/// Fields are nullable primitives rather than domain value objects or
/// `Optional`, because this type crosses a serialisation boundary — absent
/// fields become JSON nulls.
///
/// Lock state is deliberately absent; it governs writes and is not part of the
/// public read surface.
///
/// @param id the catalogue id in prefixed form, e.g. `mv_42`
/// @param version the current optimistic-locking version, to be sent back on a
///        subsequent write
/// @param title the display title
/// @param releaseDate the release date, or `null` if unknown
/// @param score the aggregate rating, or `null` if unrated
/// @param originalLanguage the ISO 639-1 code, or `null` if unknown
/// @param overview the synopsis, or `null` if unavailable
public record MovieView(
    @NonNull String id,
    @NonNull Long version,
    @NonNull String title,
    LocalDate releaseDate,
    BigDecimal score,
    String originalLanguage,
    String overview) {

  /// Constructs a view from raw persistence values.
  ///
  /// This is the constructor the query repository projects into, which is why it
  /// takes the numeric primary key rather than the prefixed id: it converts the
  /// key on the way out so that no caller has to remember to. Everything else is
  /// passed through unchanged.
  ///
  /// @param id the numeric primary key, must be positive
  /// @param version the stored version
  /// @param title the stored title
  /// @param releaseDate the stored release date, or `null`
  /// @param score the stored rating, or `null`
  /// @param originalLanguage the stored language code, or `null`
  /// @param overview the stored synopsis, or `null`
  /// @throws IllegalArgumentException if `id` is less than `1`
  public MovieView(
      Long id, 
      long version, 
      String title, 
      LocalDate releaseDate, 
      BigDecimal score, 
      String originalLanguage,
      String overview) {
    this(
        MoviePublicId.from(id).value(), 
        version, 
        title, 
        releaseDate, 
        score, 
        originalLanguage,
        overview);
  }
}
