package com.erdouglass.emdb.media.person.application.service;

import com.erdouglass.emdb.media.person.PersonCommand;
import com.erdouglass.emdb.media.person.domain.Biography;
import com.erdouglass.emdb.media.person.domain.BirthDate;
import com.erdouglass.emdb.media.person.domain.DeathDate;
import com.erdouglass.emdb.media.person.domain.Gender;
import com.erdouglass.emdb.media.person.domain.Name;
import com.erdouglass.emdb.media.person.domain.PersonDetails;

final class PersonMapper {

  private PersonMapper() {}
  
  public static PersonDetails toPersonDetails(PersonCommand command) {
    return new PersonDetails(
        Name.of(command.name()),
        command.birthDate().map(BirthDate::from),
        command.deathDate().map(DeathDate::from),
        command.gender().flatMap(Gender::from),
        command.biography().map(Biography::of));
  }
}
