package com.erdouglass.emdb.media.mapper;

import jakarta.enterprise.context.ApplicationScoped;

import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.common.comand.UpdatePerson;
import com.erdouglass.emdb.common.query.PersonDto;
import com.erdouglass.emdb.media.entity.Person;

@ApplicationScoped
public class PersonMapper {

  public Person toPerson(SavePerson command) {
    var person = new Person(command.tmdbId(), command.name());
    person.birthDate(command.birthDate());
    person.deathDate(command.deathDate());
    person.gender(command.gender());
    person.birthPlace(command.birthPlace());
    person.profile(command.profile());
    person.tmdbProfile(command.tmdbProfile());
    person.biography(command.biography());
    return person;
  }
  
  public Person toPerson(UpdatePerson command) {
    var person = new Person(command.name());
    person.birthDate(command.birthDate());
    person.deathDate(command.deathDate());
    person.gender(command.gender());
    person.birthPlace(command.birthPlace());
    person.profile(command.profile());
    person.biography(command.biography());
    return person;
  }  
  
  public SavePerson toSavePerson(Person person) {
    return SavePerson.builder()
        .tmdbId(person.tmdbId())
        .name(person.name())
        .birthDate(person.birthDate().orElse(null))
        .deathDate(person.deathDate().orElse(null))
        .gender(person.gender().orElse(null))
        .birthPlace(person.birthPlace().orElse(null))
        .profile(person.profile().orElse(null))
        .tmdbProfile(person.tmdbProfile().orElse(null))
        .biography(person.biography().orElse(null))
        .build();
  }
  
  public PersonDto toPersonDto(Person person) {
    return PersonDto.builder()
        .id(person.id())
        .tmdbId(person.tmdbId())
        .name(person.name())
        .birthDate(person.birthDate().orElse(null))
        .deathDate(person.deathDate().orElse(null))
        .gender(person.gender().orElse(null))
        .birthPlace(person.birthPlace().orElse(null))
        .profile(person.profile().map(p -> String.format("%s.jpg", p)).orElse(null))
        .biography(person.biography().orElse(null))
        .build();
  }
  
}
