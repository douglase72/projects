package com.erdouglass.emdb.media.movie.application.port.in;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

import com.erdouglass.emdb.media.UpsertMovieCommand;

/// The intended state of a title, addressed by catalogue id and guarded by a
/// version.
///
/// The editing counterpart to `SaveMovieCommand`: same body, different
/// addressing. Because the caller supplies the version it read, this command can
/// be refused as stale, which is what makes it safe for interactive editing
/// where ingestion's last-writer-wins would lose work.
///
/// Replacement semantics, as with every write command — an empty component
/// clears the field rather than preserving it.
///
/// The identifiers are held in wire form and converted downstream, so a
/// malformed catalogue id fails in the service rather than on construction.
///
/// @param publicId the catalogue id of the title to edit, in prefixed form
/// @param version the version the caller last read
/// @param title the display title, required
/// @param releaseDate the release date in ISO-8601 form, empty to clear
/// @param score the rating from 0 to 10, empty to clear
/// @param originalLanguage the ISO 639-1 code, empty to clear
/// @param overview the synopsis, empty to clear
public record UpdateMovieCommand(
    String publicId,
    Long version,
    String title,
    Optional<String> releaseDate,
    Optional<BigDecimal> score,
    Optional<String> originalLanguage,
    Optional<String> overview) implements UpsertMovieCommand {

  public UpdateMovieCommand {
    Objects.requireNonNull(publicId, "publicId is required");
    Objects.requireNonNull(version, "version is required");    
    Objects.requireNonNull(title, "title is reqired");
  }
}
