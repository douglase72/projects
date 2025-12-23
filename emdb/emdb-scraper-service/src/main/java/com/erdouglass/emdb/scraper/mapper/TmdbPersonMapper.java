package com.erdouglass.emdb.scraper.mapper;

import jakarta.enterprise.context.ApplicationScoped;

import com.erdouglass.emdb.common.Gender;
import com.erdouglass.emdb.common.PersonCreateDto;
import com.erdouglass.emdb.scraper.query.TmdbPersonDto;

@ApplicationScoped
public class TmdbPersonMapper {

  public PersonCreateDto toPersonCreateDto(TmdbPersonDto tmdbPerson) {
    return PersonCreateDto.builder()
        .tmdbId(tmdbPerson.id())
        .name(tmdbPerson.name())
        .birthDate(tmdbPerson.birthday())
        .deathDate(tmdbPerson.deathday())
        .gender(Gender.from(tmdbPerson.gender()))
        .birthPlace(tmdbPerson.place_of_birth())
        .profile(tmdbPerson.profile_path())
        .biography(tmdbPerson.biography())
        .build();
  }
  
}
