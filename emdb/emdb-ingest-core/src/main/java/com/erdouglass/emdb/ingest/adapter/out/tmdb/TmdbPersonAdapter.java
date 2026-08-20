package com.erdouglass.emdb.ingest.adapter.out.tmdb;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.ingest.application.dto.Person;
import com.erdouglass.emdb.ingest.application.port.out.PersonSource;
import com.erdouglass.emdb.media.TmdbId;

@ApplicationScoped
class TmdbPersonAdapter implements PersonSource {

  @Inject
  @RestClient
  TmdbClient client;
  
  @Override
  public Person extract(TmdbId tmdbId) {
    var tmdbPerson = client.findPersonById(tmdbId.value());
    var gender = switch (tmdbPerson.gender()) {
      case 1 -> "Female";
      case 2 -> "Male";
      case 3 -> "Non-Binary";
      default -> null;
    };
    var person = Person.builder()
        .tmdbId(TmdbId.of(tmdbPerson.id()))
        .name(tmdbPerson.name())
        .birthDate(Optional.ofNullable(tmdbPerson.birthday()))
        .deathDate(Optional.ofNullable(tmdbPerson.deathday()))
        .gender(Optional.ofNullable(gender))
        .biography(Optional.ofNullable(tmdbPerson.biography()))
        .build();
    return person;
  }
}
