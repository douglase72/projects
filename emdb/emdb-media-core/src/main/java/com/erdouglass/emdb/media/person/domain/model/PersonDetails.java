package com.erdouglass.emdb.media.person.domain.model;

import java.util.Objects;

import lombok.Builder;

@Builder
public record PersonDetails(
    Name name,
    BirthDate birthDate,
    DeathDate deathDate,
    Gender gender,
    Biography biography) {

  public PersonDetails {
    Objects.requireNonNull(name, "name is required");
    if (birthDate != null && deathDate !=null) {
      if (deathDate.value().isBefore(birthDate.value())) {
        throw new IllegalArgumentException("death date %s precedes birth date %s"
            .formatted(deathDate.toLocalDate(), birthDate.toLocalDate()));       
      }
    }
  }
}
