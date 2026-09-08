package com.erdouglass.emdb.media.person.application.port.in;

import java.util.Objects;

import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.kernel.Version;
import com.erdouglass.emdb.media.person.domain.model.PersonDetails;

public record UpdatePersonCommand(PublicId id, Version version, PersonDetails details) {

  public UpdatePersonCommand {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(version, "version is required");
    Objects.requireNonNull(details, "details are required");
  }
  
  public static UpdatePersonCommand of(PublicId id, Version version, PersonDetails details) {
    return new UpdatePersonCommand(id, version, details);
  }
}
