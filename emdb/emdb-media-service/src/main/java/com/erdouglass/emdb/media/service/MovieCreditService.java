package com.erdouglass.emdb.media.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.MovieCreditCreateDto;
import com.erdouglass.emdb.media.entity.Movie;
import com.erdouglass.emdb.media.entity.MovieCredit;
import com.erdouglass.emdb.media.entity.Person;
import com.erdouglass.emdb.media.mapper.PersonMapper;
import com.erdouglass.emdb.media.query.MovieCreditStatus;
import com.erdouglass.emdb.media.query.StatusCode;
import com.erdouglass.emdb.media.repository.MovieCreditRepository;
import com.erdouglass.emdb.media.repository.MovieRepository;
import com.erdouglass.webservices.ResourceNotFoundException;

@ApplicationScoped
public class MovieCreditService {
  private static final Logger LOGGER = Logger.getLogger(MovieCreditService.class);
  
  @Inject
  MovieCreditRepository creditRepository;
  
  @Inject
  MovieRepository movieRepository;
  
  @Inject
  PersonMapper mapper;
  
  @Inject
  PersonService personService;
  
  @Transactional
  public List<MovieCreditStatus> synchronizeAll(
      @NotNull @Positive Long movieId, 
      @NotEmpty List<@Valid MovieCreditCreateDto> credits) {
    List<MovieCreditStatus> results = new ArrayList<>();
    var movie = movieRepository.findById(movieId)
        .orElseThrow(() -> new ResourceNotFoundException("Movie not found: " + movieId));    
    var people = personService.createAll(credits.stream()
        .map(c -> mapper.toPerson(c.person())).toList()).stream()
        .collect(Collectors.toMap(s ->  s.person().tmdbId(), s -> s.person()));
    var existingCredits = creditRepository.findAll(movieId).stream()
        .collect(Collectors.toMap(c -> c.person().tmdbId(), Function.identity()));
    var creditsToInsert = new ArrayList<MovieCredit>();
    var creditsToUpdate = new ArrayList<MovieCredit>();
    
    for (var credit : credits) {
      var person = Optional.ofNullable(people.get(credit.person().tmdbId()))
          .orElseThrow(() -> new ResourceNotFoundException("Person not found: " + credit.person().tmdbId()));
      var newCredit = toMovieCredit(credit, movie, person);
      var existingCredit = existingCredits.remove(credit.person().tmdbId());
      if (existingCredit == null) {
        creditsToInsert.add(newCredit);
        results.add(new MovieCreditStatus(newCredit, StatusCode.CREATED));
      } else {
        creditsToUpdate.add(existingCredit);
        results.add(new MovieCreditStatus(existingCredit, StatusCode.UPDATED));
      }
    }
    
    if (!existingCredits.isEmpty()) {
      int count = creditRepository.deleteByTmdbIdIn(existingCredits.keySet().stream().toList());
      LOGGER.infof("Deleted: %d movie credits.", count);
    }
    
    if (!creditsToInsert.isEmpty()) {
      var insertedCredits = creditRepository.insertAll(creditsToInsert);
      LOGGER.infof("Inserted: %d movie credits", insertedCredits.size());
    }
    
    if (!creditsToUpdate.isEmpty()) {
      var updatedCredits = creditRepository.updateAll(creditsToUpdate);
      LOGGER.infof("Updated: %d movie credits", updatedCredits.size());
    }
    return results;
  }
  
  @Transactional
  public List<MovieCredit> findAll(Long id) {
    return creditRepository.findAll(id);
  }
  
  @Transactional
  int deleteByIdIn(List<Long> ids) {
    return creditRepository.deleteByIdIn(ids);
  }
  
  private MovieCredit toMovieCredit(MovieCreditCreateDto dto, Movie movie, Person person) {
    var credit = new MovieCredit(dto.tmdbId());
    credit.type(dto.type());
    credit.movie(movie);
    credit.person(person);
    credit.role(dto.role());
    credit.order(dto.order());
    return credit;
  }

}
