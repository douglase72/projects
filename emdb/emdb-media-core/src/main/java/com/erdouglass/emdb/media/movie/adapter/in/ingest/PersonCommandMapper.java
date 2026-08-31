package com.erdouglass.emdb.media.movie.adapter.in.ingest;

import com.erdouglass.emdb.media.api.LoadPersonCommand;
import com.erdouglass.emdb.media.kernel.Name;
import com.erdouglass.emdb.media.kernel.TmdbId;
import com.erdouglass.emdb.media.person.application.port.in.SavePersonCommand;
import com.erdouglass.emdb.media.person.domain.model.Biography;
import com.erdouglass.emdb.media.person.domain.model.BirthDate;
import com.erdouglass.emdb.media.person.domain.model.DeathDate;
import com.erdouglass.emdb.media.person.domain.model.Gender;
import com.erdouglass.emdb.media.person.domain.model.PersonDetails;

final class PersonCommandMapper {

  private PersonCommandMapper() { }
  
  public static SavePersonCommand toSavePersonCommand(LoadPersonCommand command) {
    var details = PersonDetails.builder()
        .name(Name.of(command.name()))
        .birthDate(command.birthDate() != null ? BirthDate.of(command.birthDate()) : null)
        .deathDate(command.deathDate() != null ? DeathDate.of(command.deathDate()) : null)
        .gender(command.gender() != null ? Gender.from(command.gender()) : null)
        .biography(command.biography() != null ? Biography.of(command.biography()) : null)
        .build();
    return SavePersonCommand.of(TmdbId.of(command.tmdbId()), details);
  }
}
