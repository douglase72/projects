package com.erdouglass.emdb.media.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.comand.SaveMovie.Credits;
import com.erdouglass.emdb.common.comand.UpdateMovieCredit;
import com.erdouglass.emdb.media.entity.Movie;
import com.erdouglass.emdb.media.entity.MovieCredit;
import com.erdouglass.emdb.media.entity.Person;
import com.erdouglass.emdb.media.mapper.MovieCreditMapper;
import com.erdouglass.emdb.media.repository.MovieCreditRepository;
import com.erdouglass.webservices.ResourceNotFoundException;

@ApplicationScoped
public class MovieCreditService {
  private static final Logger LOGGER = Logger.getLogger(MovieCreditService.class);
  
  @Inject
  MovieCreditMapper mapper;
  
  @Inject
  MovieCreditRepository repository;
  
  @Transactional
  public boolean saveAll(Movie movie, Map<Integer, Person> people, Credits credits) {
    List<MovieCredit> creditsToInsert = new ArrayList<>();
    List<MovieCredit> creditsToUpdate = new ArrayList<>();
    var existingCredits = repository.findByMovieId(movie.getId()).stream()
        .collect(Collectors.toMap(c -> c.getPerson().getTmdbId() + "-" + c.getType(), Function.identity()));
    
    List<MovieCredit> allCredits = new ArrayList<>(); 
    for (var credit : credits.cast()) {
      var person = Optional.ofNullable(people.get(credit.tmdbId()))
          .orElseThrow(() -> new ResourceNotFoundException("Person not found: " + credit.tmdbId())); 
      allCredits.add(mapper.toMovieCredit(movie, person, credit));
    }
    
    for (var credit : credits.crew()) {
      var person = Optional.ofNullable(people.get(credit.tmdbId()))
          .orElseThrow(() -> new ResourceNotFoundException("Person not found: " + credit.tmdbId())); 
      allCredits.add(mapper.toMovieCredit(movie, person, credit));
    }
    
    for (var credit : allCredits) {
      var key = credit.getPerson().getTmdbId() + "-" + credit.getType();
      var existingCredit = existingCredits.remove(key);
      if (existingCredit == null) {
        creditsToInsert.add(credit);
      } else if (!existingCredit.isEqualTo(credit)) {
        mapper.merge(credit, existingCredit);
        creditsToUpdate.add(existingCredit);
      }
    }
        
    if (!existingCredits.isEmpty()) {
      repository.deleteAll(new ArrayList<>(existingCredits.values()));
      LOGGER.infof("Deleted: %d movie credits.", existingCredits.size());
    }
    
    if (!creditsToInsert.isEmpty()) {
      var insertedCredits = repository.insertAll(creditsToInsert);
      LOGGER.infof("Inserted: %d movie credits", insertedCredits.size());
    }
    
    if (!creditsToUpdate.isEmpty()) {
      var updatedCredits = repository.updateAll(creditsToUpdate);
      LOGGER.infof("Updated: %d movie credits", updatedCredits.size());
    } 
    return !creditsToInsert.isEmpty() || !creditsToUpdate.isEmpty() || !existingCredits.isEmpty();
  }
  
  @Transactional
  public List<MovieCredit> findByMovieId(Long id) {
    return repository.findByMovieId(id);
  }
  
  @Transactional
  public MovieCredit update(Long id, UUID creditId, UpdateMovieCredit command) {
    var credit = repository.findById(creditId)
        .orElseThrow(() -> new ResourceNotFoundException("No credit found with id: " + creditId));
    if (!credit.getMovie().getId().equals(id)) {
      throw new IllegalArgumentException("Credit mismatch: " + creditId);
    }
    mapper.merge(command, credit);
    return repository.update(credit);
  }
  
  @Transactional
  public void deleteAll(List<MovieCredit> credits) {
    repository.deleteAll(credits);
  }

}
