package com.erdouglass.emdb.media.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.common.PersonCreateDto;
import com.erdouglass.emdb.common.query.PersonDto;
import com.erdouglass.emdb.media.entity.Person;

@ApplicationScoped
public class PersonMapper {
  
  @Inject
  PersonCreditMapper mapper;
  
  public Person toPerson(PersonCreateDto dto) {
    var person = new Person(dto.tmdbId(), dto.name());
    person.birthDate(dto.birthDate());
    person.deathDate(dto.deathDate());
    person.gender(dto.gender());
    person.birthPlace(dto.birthPlace());
    person.profile(dto.profile());
    person.biography(dto.biography());
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
        .cast(person.cast().stream().map(mapper::toPersonCreditDto).toList())
        .crew(person.crew().stream().map(mapper::toPersonCreditDto).toList())
        .build();
  }

}
