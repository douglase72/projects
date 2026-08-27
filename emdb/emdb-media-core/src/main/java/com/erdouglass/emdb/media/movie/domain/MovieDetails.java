package com.erdouglass.emdb.media.movie.domain;

import java.util.Objects;

import com.erdouglass.emdb.media.kernel.LanguageCode;
import com.erdouglass.emdb.media.kernel.Overview;
import com.erdouglass.emdb.media.kernel.Score;
import com.erdouglass.emdb.media.kernel.Title;

import lombok.Builder;

/// The mutable content of a title — everything about a movie that can change
/// without it becoming a different movie.
///
/// Kept separate from [Movie] so that identity and lifecycle live in the
/// aggregate while content stays an immutable value. Replacing details is
/// therefore a single assignment, and comparing two revisions is a plain value
/// comparison, which is what [MovieField#diff] relies on.
///
/// Only the title is required. The remaining components are optional both in the
/// sense that they may be absent and in the sense that absence is meaningful:
/// an empty component is a field the catalogue does not know, and clearing a
/// field is a recordable change.
///
/// @param title the display title, never `null`
/// @param releaseDate the theatrical or first-publication date, if known
/// @param score the aggregate rating from 0 to 10, if rated
/// @param originalLanguage the language the title was produced in, if known
/// @param overview the synopsis, if available
/// @param credits the movie credits
@Builder
public record MovieDetails(
    Title title,
    ReleaseDate releaseDate,
    Score score,
    LanguageCode originalLanguage,
    Overview overview) {
  
  public MovieDetails {
    Objects.requireNonNull(title, "title is required");
  }
}
