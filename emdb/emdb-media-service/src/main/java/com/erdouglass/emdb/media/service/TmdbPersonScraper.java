package com.erdouglass.emdb.media.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.media.annotation.ExtractionStatus;
import com.erdouglass.emdb.media.api.Image;
import com.erdouglass.emdb.media.api.command.SavePerson;
import com.erdouglass.emdb.media.client.TmdbPersonClient;
import com.erdouglass.emdb.media.entity.Person;
import com.erdouglass.emdb.media.mapper.PersonMapper;
import com.erdouglass.emdb.media.query.TmdbPerson;
import com.google.common.base.Objects;

@ApplicationScoped
public class TmdbPersonScraper {

  @Inject
  @RestClient
  TmdbPersonClient client;
  
  @Inject
  TmdbImageService imageService;
  
  @Inject
  PersonMapper mapper;
  
  @ExtractionStatus
  public SavePerson extract(@NotNull Person person) {
    var tmdbPerson = client.findById(person.getTmdbId());
    var profile = extractProfile(person, tmdbPerson);
    return mapper.toSavePerson(tmdbPerson, profile);
  }
  
  private Image extractProfile(Person person, TmdbPerson tmdbPerson) {
    var image = Image.of(person.getProfile(), person.getTmdbProfile());
    if (person.getProfile() == null || !Objects.equal(person.getTmdbProfile(), tmdbPerson.profile_path())) {
      image = Image.of(imageService.save(tmdbPerson.profile_path()), tmdbPerson.profile_path());
    }
    return image;
  }
}
