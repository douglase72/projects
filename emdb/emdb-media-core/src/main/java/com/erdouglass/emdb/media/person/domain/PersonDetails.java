package com.erdouglass.emdb.media.person.domain;

import java.util.Objects;
import java.util.Optional;

/// The mutable content of a person — everything about a person that can change
/// without it becoming a different person.
///
/// Kept separate from [Person] so that identity and lifecycle live in the
/// aggregate while content stays an immutable value. Replacing details is
/// therefore a single assignment, and comparing two revisions is a plain value
/// comparison, which is what [PersonField#diff] relies on.
///
/// Only the name is required. The remaining components are optional both in the
/// sense that they may be absent and in the sense that absence is meaningful:
/// an empty component is a field the catalogue does not know, and clearing a
/// field is a recordable change.
///
/// @param name the display name, never `null`
/// @param birthDate the date of birth, if known
/// @param deathDate the date of birth, if known
/// @param gender the gender, if known
/// @param biography the persons biography, if available
public record PersonDetails(
    Name name,
    Optional<BirthDate> birthDate,
    Optional<DeathDate> deathDate,
    Optional<Gender> gender,
    Optional<Biography> biography) {

  public static Builder builder() { return new Builder(); }
  
  public static final class Builder {
    private Name name;
    private BirthDate birthDate;
    private DeathDate deathDate;
    private Gender gender;
    private Biography biography;
    
    private Builder() {}
    
    public PersonDetails build() {
      Objects.requireNonNull(name, "name is required");
      if (birthDate != null && deathDate != null) {
        if (deathDate.value().isBefore(birthDate.value())) {
          throw new IllegalArgumentException("death date %s precedes birth date %s"
              .formatted(deathDate.toLocalDate(), birthDate.toLocalDate()));
        }
      }
      return new PersonDetails(
          name, 
          Optional.ofNullable(birthDate), 
          Optional.ofNullable(deathDate), 
          Optional.ofNullable(gender), 
          Optional.ofNullable(biography));
    }
    
    public Builder biography(Biography biography) {
      this.biography = biography;
      return this;
    }
    
    public Builder birthDate(BirthDate birthDate) {
      this.birthDate = birthDate;
      return this;
    }
    
    public Builder deathDate(DeathDate deathDate) {
      this.deathDate = deathDate;
      return this;
    }
    
    public Builder gender(Gender gender) {
      this.gender = gender;
      return this;
    }
    
    public Builder name(Name name) {
      this.name = name;
      return this;
    }
  }
}
