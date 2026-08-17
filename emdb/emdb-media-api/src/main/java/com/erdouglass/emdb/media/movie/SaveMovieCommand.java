package com.erdouglass.emdb.media.movie;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

import com.erdouglass.emdb.media.TmdbId;

/// The intended state of a title, addressed by its TMDB id.
///
/// Replacement semantics, not patch semantics: the command describes the whole
/// title, so an empty component clears the corresponding field and is recorded
/// in the audit trail as a removal. There is no way to say "leave this one
/// alone" — a caller that wants to preserve a field must send its current value.
///
/// Values arrive here in their wire form (strings, `BigDecimal`) and are
/// converted to domain value objects by the mapping layer, so a syntactically
/// invalid release date or an out-of-range score fails during mapping rather
/// than on construction of this command.
///
/// @param tmdbId the natural key of the title, required
/// @param title the display title, required; validated for length and blankness
///        when converted to `Title`
/// @param releaseDate the release date in ISO-8601 form, empty to clear
/// @param score the aggregate rating from 0 to 10, empty to clear
/// @param originalLanguage the ISO 639-1 code of the original language, empty to
///        clear
/// @param overview the synopsis, empty to clear
public record SaveMovieCommand(
    TmdbId tmdbId,
    String title,
    Optional<String> releaseDate,
    Optional<BigDecimal> score,
    Optional<String> originalLanguage,
    Optional<String> overview) implements MovieCommand {
  
  public SaveMovieCommand {
    Objects.requireNonNull(tmdbId, "tmdbId must not be null");
    Objects.requireNonNull(title, "title must not be null");
  }
}
