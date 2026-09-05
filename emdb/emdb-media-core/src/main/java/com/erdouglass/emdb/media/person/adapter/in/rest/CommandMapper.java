package com.erdouglass.emdb.media.person.adapter.in.rest;

import com.erdouglass.emdb.media.api.TmdbId;
import com.erdouglass.emdb.media.kernel.Name;
import com.erdouglass.emdb.media.person.application.port.in.SavePersonCommand;
import com.erdouglass.emdb.media.person.domain.model.Biography;
import com.erdouglass.emdb.media.person.domain.model.BirthDate;
import com.erdouglass.emdb.media.person.domain.model.DeathDate;
import com.erdouglass.emdb.media.person.domain.model.Gender;
import com.erdouglass.emdb.media.person.domain.model.PersonDetails;

final class CommandMapper {

  private CommandMapper() { }
  
  public static SavePersonCommand toSavePersonCommand(Integer tmdbId, SavePersonRequest request) { 
    var details = PersonDetails.builder()
        .name(Name.of(request.name()))
        .birthDate(request.birthDate() != null ? BirthDate.from(request.birthDate()) : null)
        .deathDate(request.deathDate() != null ? DeathDate.from(request.deathDate()) : null)
        .gender(request.gender() != null ? Gender.from(request.gender()) : null)
        .biography(request.biography() != null ? Biography.of(request.biography()) : null)
        .build();
    return SavePersonCommand.of(TmdbId.of(tmdbId), details);
  }
}
