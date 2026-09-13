package com.erdouglass.emdb.media.person.application.service;

import com.erdouglass.emdb.media.SavePersonCommand;
import com.erdouglass.emdb.media.person.domain.model.Biography;
import com.erdouglass.emdb.media.person.domain.model.BirthDate;
import com.erdouglass.emdb.media.person.domain.model.DeathDate;
import com.erdouglass.emdb.media.person.domain.model.Gender;
import com.erdouglass.emdb.media.person.domain.model.Name;
import com.erdouglass.emdb.media.person.domain.model.PersonDetails;

final class PersonDetailsMapper {

  private PersonDetailsMapper() { }
  
  public static PersonDetails toPersonDetails(SavePersonCommand command) {
    return PersonDetails.builder()
        .name(command.name() != null ? Name.of(command.name()) : null)
        .birthDate(command.birthDate() != null ? BirthDate.from(command.birthDate()) : null)
        .deathDate(command.deathDate() != null ? DeathDate.from(command.deathDate()) : null)
        .gender(command.gender() != null ? Gender.from(command.gender()) : null)
        .biography(command.biography() != null ? Biography.of(command.biography()) : null)
        .build();
  }
}
