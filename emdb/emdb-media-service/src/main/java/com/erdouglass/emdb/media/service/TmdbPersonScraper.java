package com.erdouglass.emdb.media.service;

import java.util.UUID;

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

@ApplicationScoped
public class TmdbPersonScraper {

  @Inject
  @RestClient
  TmdbPersonClient client;
  
  @Inject
  PersonMapper mapper;
  
  @ExtractionStatus
  public SavePerson extract(@NotNull Person person) {
    var tmdbPerson = client.findById(person.getTmdbId());
    var profile = Image.of(UUID.randomUUID(), tmdbPerson.profile_path());    
    return mapper.toSavePerson(tmdbPerson, profile);
  }
}
