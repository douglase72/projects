package com.erdouglass.emdb.media.person;

import java.util.Objects;
import java.util.Optional;

import com.erdouglass.emdb.media.TmdbId;

/// The intended state of a person, addressed by its TMDB id.
///
/// Replacement semantics, not patch semantics: the command describes the whole
/// person, so an empty component clears the corresponding field and is recorded
/// in the audit trail as a removal. There is no way to say "leave this one
/// alone" — a caller that wants to preserve a field must send its current value.
///
/// @param tmdbId the natural key of the person, required
/// @param name the display name, required; validated for length and blankness
///        when converted to `Name`
/// @param birthDate the birth date in ISO-8601 form, empty to clear
/// @param deathDate the death date in ISO-8601 form, empty to clear
/// @param gender the persons gender, required
/// @param biography the biography, empty to clear
public record SavePersonCommand(
    TmdbId tmdbId,
    String name,
    Optional<String> birthDate,
    Optional<String> deathDate,
    Optional<String> gender,
    Optional<String> biography) implements PersonCommand {

  public SavePersonCommand {
    Objects.requireNonNull(tmdbId, "TMDB id is required");
    Objects.requireNonNull(name, "name is required");
  }
}
