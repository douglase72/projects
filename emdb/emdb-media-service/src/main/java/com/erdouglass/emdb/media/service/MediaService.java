package com.erdouglass.emdb.media.service;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.common.service.IngestStatusService;
import com.erdouglass.emdb.media.entity.Show;
import com.erdouglass.emdb.scraper.service.TmdbImageService;

public abstract class MediaService {
  
  @Inject
  TmdbImageService imageService;  
  
  @Inject
  IngestStatusService statusService;  
  
  @Inject
  Validator validator;
  
  public abstract Duration ingest(@NotNull @Positive Integer tmdbId, @NotNull UUID jobId);
  
  protected void deleteShowImages(Show existingShow, Show newShow) {
    if (!Objects.equals(existingShow.tmdbBackdrop().orElse(null), newShow.tmdbBackdrop().orElse(null))) {
      existingShow.backdrop().ifPresent(imageService::delete);
    }
    if (!Objects.equals(existingShow.tmdbPoster().orElse(null), newShow.tmdbPoster().orElse(null))) {
      existingShow.poster().ifPresent(imageService::delete);
    }    
  }
  
  protected void deletePeopleImages(List<SavePerson> newPeople, List<SavePerson> oldPeople) {
    var oldPeopleMap = oldPeople.stream()
        .collect(Collectors.toMap(SavePerson::tmdbId, Function.identity(), (p1, _) -> p1));        
    for (var newPerson : newPeople) {
      var oldPerson = oldPeopleMap.get(newPerson.tmdbId());
      if (oldPerson != null && !Objects.equals(oldPerson.tmdbProfile(), newPerson.tmdbProfile())) {
        if (oldPerson.profile() != null) {
          imageService.delete(oldPerson.profile());
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
