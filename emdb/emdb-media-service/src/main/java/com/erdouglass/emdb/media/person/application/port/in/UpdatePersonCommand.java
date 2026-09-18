package com.erdouglass.emdb.media.person.application.port.in;

import java.util.Objects;

import com.erdouglass.emdb.media.kernel.PublicId;

public record UpdatePersonCommand(
    PublicId id,
    Long version,
    String name,
    String birthDate,
    String deathDate,
    String gender,
    String biography) {

  public UpdatePersonCommand {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(version, "version is required");
    Objects.requireNonNull(name, "name is required");
  }
}
