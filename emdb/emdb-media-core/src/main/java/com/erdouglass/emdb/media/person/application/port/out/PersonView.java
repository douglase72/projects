package com.erdouglass.emdb.media.person.application.port.out;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import com.erdouglass.emdb.media.person.domain.model.Gender;

public record PersonView(    
    UUID id,
    Long version,
    String name,
    LocalDate birthDate,
    LocalDate deathDate,
    Gender gender,
    String biography) {

  public PersonView {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(version, "version is required");
    Objects.requireNonNull(name, "name is required");
  }
}
