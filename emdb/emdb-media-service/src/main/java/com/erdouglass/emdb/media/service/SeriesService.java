package com.erdouglass.emdb.media.service;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.erdouglass.emdb.common.comand.SaveSeries;
import com.erdouglass.emdb.common.comand.SaveSeries.Credits;
import com.erdouglass.emdb.media.dto.SaveResult;
import com.erdouglass.emdb.media.dto.SaveResult.SaveStatus;
import com.erdouglass.emdb.media.entity.Person;
import com.erdouglass.emdb.media.entity.Series;
import com.erdouglass.emdb.media.logging.LogDuration;
import com.erdouglass.emdb.media.mapper.SeriesMapper;
import com.erdouglass.emdb.media.repository.SeriesRepository;

@ApplicationScoped
public class SeriesService {
  
  @Inject
  SeriesMapper mapper;
  
  @Inject
  PersonService personService;
  
  @Inject
  SeriesRepository repository;
  
  @Transactional
  @LogDuration("Saved:")
  public SaveResult<Series> save(@NotNull @Valid SaveSeries command) {
    var status = SaveStatus.UNCHANGED;
    var existingSeries = repository.findByTmdbId(command.tmdbId()).orElse(null);
    if (existingSeries == null) {
      existingSeries = repository.insert(mapper.toSeries(command));
      status = SaveStatus.CREATED;
    } else if (!isEqual(command, existingSeries)) {
      mapper.merge(command, existingSeries);
      repository.update(existingSeries);
      status = SaveStatus.UPDATED;
    }
    var savedPeople = personService.saveAll(command.people()).stream()
        .map(SaveResult::entity)
        .collect(Collectors.toMap(Person::getTmdbId, Function.identity()));
    saveCredits(existingSeries, savedPeople, command.credits());
    return SaveResult.of(status, existingSeries);
  }
  
  private boolean isEqual(SaveSeries command, Series series) {
    if (command == null || series == null) return false;
    return Objects.equals(command.tmdbId(), series.getTmdbId())
        && Objects.equals(command.title(), series.getTitle())
        && Objects.equals(command.score(), series.getScore())
        && Objects.equals(command.status(), series.getStatus())
        && Objects.equals(command.type(), series.getType())
        && Objects.equals(command.backdrop(), series.getBackdrop())
        && Objects.equals(command.poster(), series.getPoster())
        && Objects.equals(command.homepage(), series.getHomepage())
        && Objects.equals(command.originalLanguage(), series.getOriginalLanguage())
        && Objects.equals(command.overview(), series.getOverview());
  }
  
  private void saveCredits(Series series, Map<Integer, Person> people, Credits credits) {
    
  }

}
