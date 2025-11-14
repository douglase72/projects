package com.erdouglass.emdb.scraper.mapper;

import com.erdouglass.emdb.common.Gender;
import com.erdouglass.emdb.common.command.PersonCreateCommand;
import com.erdouglass.emdb.scraper.dto.TmdbPerson;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TmdbPersonMapper {
  
  public PersonCreateCommand toPersonCreateCommand(TmdbPerson tmdbPerson) {
    return PersonCreateCommand.builder()
        .tmdbId(tmdbPerson.id())
        .name(tmdbPerson.name())
        .birthDate(tmdbPerson.birthday())
        .deathDate(tmdbPerson.deathday())
        .gender(Gender.from(tmdbPerson.gender()))
        .homepage(tmdbPerson.homepage())
        .birthPlace(tmdbPerson.place_of_birth())
        .profile(tmdbPerson.profile_path())
        .biography(tmdbPerson.biography())
        .aliases(tmdbPerson.also_known_as())
        .build();
  }
  
}
