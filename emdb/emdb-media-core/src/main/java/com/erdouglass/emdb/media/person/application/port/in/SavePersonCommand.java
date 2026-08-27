package com.erdouglass.emdb.media.person.application.port.in;

import java.util.Objects;
import java.util.Optional;

import com.erdouglass.emdb.media.kernel.SourceId;

/// The intended state of a person, addressed by its source id.
///
/// Replacement semantics, not patch semantics: the command describes the whole
/// person, so an empty component clears the corresponding field and is recorded
/// in the audit trail as a removal. There is no way to say "leave this one
/// alone" — a caller that wants to preserve a field must send its current value.
///
/// @param sourceId the natural key of the person, required
/// @param name the display name, required; validated for length and blankness
///        when converted to `Name`
/// @param birthDate the birth date in ISO-8601 form, empty to clear
/// @param deathDate the death date in ISO-8601 form, empty to clear
/// @param gender the persons gender, required
/// @param biography the biography, empty to clear
public record SavePersonCommand(
    SourceId sourceId,
    String name,
    Optional<String> birthDate,
    Optional<String> deathDate,
    Optional<String> gender,
    Optional<String> biography) implements PersonCommand {

  public SavePersonCommand {
    Objects.requireNonNull(sourceId, "sourceId id is required");
    Objects.requireNonNull(name, "name is required");
  }
}
