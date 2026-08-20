package com.erdouglass.emdb.media.person;

import java.util.Optional;

/// The mutable body shared by every write command.
///
/// [SavePersonCommand] and `UpdatePersonCommand` differ only in how they address
/// the target — TMDB id versus catalogue id plus version — so the fields that
/// actually land on the aggregate are factored out here. Mapping code can then
/// build `PersonDetails` from either command without knowing which one it holds.
///
/// Every implementation carries replacement semantics: an empty optional means
/// "clear this field", never "skip this field".
public interface PersonCommand {

  /// {@return the display name; required, and never blank once validated}
  String name();
  
  /// {@return the birth date in ISO-8601 form, or empty to clear it}
  Optional<String> birthDate();
  
  /// {@return the death date in ISO-8601 form, or empty to clear it}
  Optional<String> deathDate();
  
  /// {@return the gender, or empty to clear it}
  Optional<String> gender();
  
  /// {@return the biography, or empty to clear it}
  Optional<String> biography();
}
