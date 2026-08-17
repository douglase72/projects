package com.erdouglass.emdb.media.movie.application.port.out;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.erdouglass.emdb.media.movie.domain.Movie;

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
    Long id,
    Long version,
    String title,
    LocalDate releaseDate,
    BigDecimal score,
    String originalLanguage,
    String overview) {}
