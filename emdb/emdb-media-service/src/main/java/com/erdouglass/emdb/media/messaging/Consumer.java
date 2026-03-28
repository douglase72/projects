package com.erdouglass.emdb.media.messaging;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.erdouglass.emdb.media.entity.Person;
import com.erdouglass.emdb.media.entity.Show;
import com.erdouglass.emdb.scraper.service.TmdbImageService;

public abstract class Consumer {
  
  @Inject
  TmdbImageService imageService;  

  @Inject
  Validator validator;
  
  public abstract void ingest(@NotNull @Positive Integer tmdbId);
  
  protected void deleteOldImages(Show existingShow, Show newShow) {
    if (!Objects.equals(existingShow.getTmdbBackdrop(), newShow.getTmdbBackdrop())) {
      var backdrop = existingShow.getBackdrop();
      if (backdrop != null) {
        imageService.delete(backdrop);
      }
    }
    
    if (!Objects.equals(existingShow.getTmdbPoster(), newShow.getTmdbPoster())) {
      var poster = existingShow.getPoster();
      if (poster != null) {
        imageService.delete(poster);
      }
    }
  }
  
  protected void deleteOldImages(List<Person> existingPeople, List<Person> newPeople) {
    var existingPeopleMap = existingPeople.stream()
        .collect(Collectors.toMap(Person::getTmdbId, Function.identity(), (p1, _) -> p1));        
    for (var newPerson : newPeople) {
      var existingPerson = existingPeopleMap.get(newPerson.getTmdbId());
      if (!Objects.equals(existingPerson.getTmdbProfile(), newPerson.getTmdbProfile())) {
        var profile = existingPerson.getProfile();
        if (profile != null) {
          imageService.delete(profile);
        }
      }
    }
  }
  
  protected <T> void validate(T command) {
    var violations = validator.validate(command);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    } 
  }
  
}
