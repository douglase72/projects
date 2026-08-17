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
    return PersonDetails.builder()
        .name(Name.of(command.name()))
        .birthDate(command.birthDate().map(BirthDate::from).orElse(null))
        .deathDate(command.deathDate().map(DeathDate::from).orElse(null))
        .gender(Gender.from(command.gender())
            .orElseThrow(() -> new IllegalArgumentException("Invalid gender: " + command.gender())))
        .biography(command.biography().map(Biography::of).orElse(null))
        .build();
  }
}
