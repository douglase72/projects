package com.erdouglass.emdb.media.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.common.comand.SaveMovie.Credits;
import com.erdouglass.emdb.common.comand.UpdateMovie;
import com.erdouglass.emdb.media.dto.SaveResult;
import com.erdouglass.emdb.media.dto.SaveResult.SaveStatus;
import com.erdouglass.emdb.media.entity.Movie;
import com.erdouglass.emdb.media.entity.MovieCredit;
import com.erdouglass.emdb.media.entity.Movie_;
import com.erdouglass.emdb.media.entity.Person;
import com.erdouglass.emdb.media.logging.LogDuration;
import com.erdouglass.emdb.media.mapper.MovieCreditMapper;
import com.erdouglass.emdb.media.mapper.MovieMapper;
import com.erdouglass.emdb.media.repository.MovieCreditRepository;
import com.erdouglass.emdb.media.repository.MovieRepository;
import com.erdouglass.webservices.ResourceNotFoundException;

@ApplicationScoped
public class MovieService {
  private static final Logger LOGGER = Logger.getLogger(MovieService.class);
  
  @Inject
  MovieCreditMapper creditMapper;
  
  @Inject
  MovieCreditRepository creditRepository;
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  PersonService personService;
  
  @Inject
  MovieRepository repository;
  
  @Transactional
  @LogDuration("Saved:")
  public SaveResult<Movie> save(@NotNull @Valid SaveMovie command) {
    var status = SaveStatus.UNCHANGED;
    var existingMovie = repository.findByTmdbId(command.tmdbId()).orElse(null);
    if (existingMovie == null) {
      existingMovie = repository.insert(mapper.toMovie(command));
      status = SaveStatus.CREATED;
    } else if (!isEqual(command, existingMovie)) {
      mapper.merge(command, existingMovie);
      repository.update(existingMovie);
      status = SaveStatus.UPDATED;
    }
    var savedPeople = command.people().isEmpty() ? Map.<Integer, Person>of() 
        : personService.saveAll(command.people()).stream()
        .map(SaveResult::entity)
        .collect(Collectors.toMap(Person::getTmdbId, Function.identity()));
    boolean creditsChanged = saveCredits(existingMovie, savedPeople, command.credits());
    if (status == SaveStatus.UNCHANGED && creditsChanged) {
      status = SaveStatus.UPDATED;
    }
    return SaveResult.of(status, existingMovie);
  }
  
  @Transactional
  @LogDuration("Found:")
  public Optional<Movie> findById(@NotNull @Positive Long id, String append) {
    var movie = repository.findById(id);
    movie.ifPresent(m -> {
      m.setCredits(List.of());
      if (append != null && append.contains(Movie_.CREDITS)) {
        m.setCredits(creditRepository.findByMovieId(id));
      }
    });
    return movie;
  }
  
  @Transactional
  @LogDuration("Found:")
  public Optional<Movie> findByTmdbId(@NotNull @Positive Integer id) {
    return repository.findByTmdbId(id);
  }  
  
  @Transactional
  @LogDuration("Updated:")
  public Movie update(Long id, UpdateMovie command) {
    var existingMovie = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + id));
    existingMovie.setCredits(List.of());
    mapper.merge(command, existingMovie);
    return repository.update(existingMovie);
  }
  
  @Transactional
  @LogDuration(value = "Deleted:", subject = "movie")
  public void delete(Long id) {
    creditRepository.deleteAll(creditRepository.findByMovieId(id));
    repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + id));
    repository.deleteById(id);
  }
  
  private boolean isEqual(SaveMovie command, Movie movie) {
    if (command == null || movie == null) return false;
    return Objects.equals(command.tmdbId(), movie.getTmdbId())
        && Objects.equals(command.title(), movie.getTitle())
        && Objects.equals(command.releaseDate(), movie.getReleaseDate())
        && Objects.equals(command.score(), movie.getScore())
        && Objects.equals(command.status(), movie.getStatus())
        && Objects.equals(command.runtime(), movie.getRuntime())
        && Objects.equals(command.budget(), movie.getBudget())
        && Objects.equals(command.backdrop(), movie.getBackdrop())
        && Objects.equals(command.poster(), movie.getPoster())
        && Objects.equals(command.homepage(), movie.getHomepage())
        && Objects.equals(command.originalLanguage(), movie.getOriginalLanguage())
        && Objects.equals(command.overview(), movie.getOverview());
  }
  
  private boolean saveCredits(Movie movie, Map<Integer, Person> people, Credits credits) {
    List<MovieCredit> creditsToInsert = new ArrayList<>();
    List<MovieCredit> creditsToUpdate = new ArrayList<>();
    var existingCredits = creditRepository.findByMovieId(movie.getId()).stream()
        .collect(Collectors.toMap(c -> c.getPerson().getTmdbId() + "-" + c.getType(), Function.identity()));
    
    List<MovieCredit> allCredits = new ArrayList<>(); 
    for (var credit : credits.cast()) {
      var person = Optional.ofNullable(people.get(credit.tmdbId()))
          .orElseThrow(() -> new ResourceNotFoundException("Person not found: " + credit.tmdbId())); 
      allCredits.add(creditMapper.toMovieCredit(movie, person, credit));
    }
    
    for (var credit : credits.crew()) {
      var person = Optional.ofNullable(people.get(credit.tmdbId()))
          .orElseThrow(() -> new ResourceNotFoundException("Person not found: " + credit.tmdbId())); 
      allCredits.add(creditMapper.toMovieCredit(movie, person, credit));
    }
    
    for (var credit : allCredits) {
      var key = credit.getPerson().getTmdbId() + "-" + credit.getType();
      var existingCredit = existingCredits.remove(key);
      if (existingCredit == null) {
        creditsToInsert.add(credit);
      } else if (!existingCredit.isEqualTo(credit)) {
        creditMapper.merge(credit, existingCredit);
        creditsToUpdate.add(existingCredit);
      }
    }
        
    if (!existingCredits.isEmpty()) {
      creditRepository.deleteAll(new ArrayList<>(existingCredits.values()));
      LOGGER.infof("Deleted: %d movie credits.", existingCredits.size());
    }
    
    if (!creditsToInsert.isEmpty()) {
      var insertedCredits = creditRepository.insertAll(creditsToInsert);
      LOGGER.infof("Inserted: %d movie credits", insertedCredits.size());
    }
    
    if (!creditsToUpdate.isEmpty()) {
      var updatedCredits = creditRepository.updateAll(creditsToUpdate);
      LOGGER.infof("Updated: %d movie credits", updatedCredits.size());
    } 
    return !creditsToInsert.isEmpty() || !creditsToUpdate.isEmpty() || !existingCredits.isEmpty();
  }
}
