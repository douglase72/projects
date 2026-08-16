package com.erdouglass.emdb.media;

import java.math.BigDecimal;
import java.util.Optional;

/// The mutable body shared by every write command.
///
/// [SaveMovieCommand] and `UpdateMovieCommand` differ only in how they address
/// the target — TMDB id versus catalogue id plus version — so the fields that
/// actually land on the aggregate are factored out here. Mapping code can then
/// build `MovieDetails` from either command without knowing which one it holds.
///
/// Every implementation carries replacement semantics: an empty optional means
/// "clear this field", never "skip this field".
public interface UpsertMovieCommand {
  
  /// {@return the display title; required, and never blank once validated}
  String title();
  
  /// {@return the release date in ISO-8601 form, or empty to clear it}
  Optional<String> releaseDate();
  
  /// {@return the aggregate rating from 0 to 10, or empty to clear it}
  Optional<BigDecimal> score();
  
  /// {@return the ISO 639-1 code of the original language, or empty to clear it}
  Optional<String> originalLanguage();
  
  /// {@return the synopsis, or empty to clear it}
  Optional<String> overview();
}
