package com.erdouglass.emdb.media.mapper;

import jakarta.enterprise.context.ApplicationScoped;

import com.erdouglass.emdb.common.query.PersonDto;
import com.erdouglass.emdb.common.request.PersonCreateRequest;
import com.erdouglass.emdb.media.entity.Person;

@ApplicationScoped
public class PersonMapper {
  
  public Person toPerson(PersonCreateRequest request) {
    var person = new Person(request.tmdbId(), request.name());
    person.birthDate(request.birthDate());
    person.deathDate(request.deathDate());
    person.gender(request.gender());
    person.birthPlace(request.birthPlace());
    person.profile(request.profile());
    person.biography(request.biography());
    return person;
  }
  
  public PersonDto toPersonDto(Person person) {
    return PersonDto.builder()
        .id(person.id())
        .name(person.name())
        .birthDate(person.birthDate().orElse(null))
        .deathDate(person.deathDate().orElse(null))
        .tmdbId(person.tmdbId())
        .gender(person.gender())
        .birthPlace(person.birthPlace().orElse(null))
        .profile(person.profile().orElse(null))
        .biography(person.biography().orElse(null))
        .build();
  }

}
