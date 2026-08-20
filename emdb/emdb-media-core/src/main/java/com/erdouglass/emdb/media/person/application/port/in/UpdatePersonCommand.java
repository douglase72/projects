package com.erdouglass.emdb.media.person.application.port.in;

import java.util.Objects;
import java.util.Optional;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.media.person.PersonCommand;
import com.erdouglass.emdb.media.person.domain.Name;

public record UpdatePersonCommand(
    String publicId,
    Long version,
    @NotBlank @Size(max = Name.MAX_LENGTH) String name,
    Optional<String> birthDate,
    Optional<String> deathDate,
    String gender,
    Optional<String> biography) implements PersonCommand {

  public UpdatePersonCommand {
    Objects.requireNonNull(publicId, "publicId is required");
    Objects.requireNonNull(version, "version is required");    
    Objects.requireNonNull(name, "name is reqired");
  }
}
