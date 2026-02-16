package com.erdouglass.emdb.media.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.MediaType;
import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.common.comand.SaveMovieCredit;
import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.common.comand.UpdateMovie;
import com.erdouglass.emdb.common.comand.UpdateMovieCredit;
import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.common.event.IngestStatusChanged.IngestSource;
import com.erdouglass.emdb.common.event.IngestStatusChanged.IngestStatus;
import com.erdouglass.emdb.media.entity.Movie;
import com.erdouglass.emdb.media.entity.MovieCredit;
import com.erdouglass.emdb.media.entity.Movie_;
import com.erdouglass.emdb.media.entity.Person;
import com.erdouglass.emdb.media.mapper.MovieCreditMapper;
import com.erdouglass.emdb.media.mapper.MovieMapper;
import com.erdouglass.emdb.media.mapper.PersonMapper;
import com.erdouglass.emdb.media.repository.MovieCreditRepository;
import com.erdouglass.emdb.media.repository.MovieRepository;
import com.erdouglass.emdb.scraper.service.TmdbMovieScraper;
import com.erdouglass.webservices.ResourceNotFoundException;

import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class MovieService extends MediaService {
  private static final Logger LOGGER = Logger.getLogger(MovieService.class);
  private static final String ROUTE_KEY = "movie.invalid";
  
  @Inject
  MovieCreditMapper creditMapper;
  
  @Inject
  MovieCreditRepository creditRepository;
  
  @Inject
  @Channel("movie-dlq-out")
  Emitter<SaveMovie> dlqEmitter;
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  PersonMapper personMapper;
  
  @Inject
  PersonService personService;
  
  @Inject
  TmdbMovieScraper scraper;
  
  @Inject
  MovieRepository repository;
  
  @Override
  public void ingest(@NotNull @Positive Integer tmdbId, @NotNull UUID jobId) {
    var existingMovie = findByTmdbId(tmdbId);
    var command = existingMovie
        .map(mapper::toSaveMovie)
        .orElseGet(() -> SaveMovie.builder().tmdbId(tmdbId).build());
    var saveCommand = scraper.scrape(command, jobId);
    
    try {
      validate(saveCommand);
      var movie = save(saveCommand);
      existingMovie.ifPresent(m -> {
        if (!Objects.equals(m.tmdbBackdrop().orElse(null), movie.tmdbBackdrop().orElse(null))) {
          m.backdrop().ifPresent(imageService::delete);
        }
        if (!Objects.equals(m.tmdbPoster().orElse(null), movie.tmdbPoster().orElse(null))) {
          m.poster().ifPresent(imageService::delete);
        } 
      });
      deleteImages(saveCommand, command.credits()); 
      statusService.send(IngestStatusChanged.builder()
          .id(jobId)
          .status(IngestStatus.COMPLETED)
          .tmdbId(tmdbId)
          .source(IngestSource.MEDIA)
          .type(MediaType.MOVIE)
          .name(movie.title())
          .emdbId(movie.id())
          .build());
    } catch (Exception e) {
      dlqEmitter.send(Message.of(saveCommand)
          .addMetadata(OutgoingRabbitMQMetadata.builder()
          .withRoutingKey(ROUTE_KEY)
          .build()));
      throw new RuntimeException(e);
    }
  }
  
  @Transactional
  public Movie save(SaveMovie command) {
    long start = System.nanoTime();
    var movie = mapper.toMovie(command);
    repository.findByTmdbId(movie.tmdbId()).ifPresent(m -> movie.id(m.id()));
    var savedMovie = repository.save(movie);
    var savedPeople = personService.saveAll(command.credits().stream()
        .map(SaveMovieCredit::person)
        .distinct()
        .toList()).stream()
        .collect(Collectors.toMap(s -> s.person().tmdbId(), s -> s.person()));
    saveCredits(savedMovie, savedPeople, command.credits());
    var et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    LOGGER.infof("Saved %s in %d ms", savedMovie, et);
    return savedMovie; 
  }
  
  @Transactional
  public Movie findById(@NotNull @Positive Long id, String append) {
    var movie = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + id));
    movie.credits(List.of());
    if (append != null) {
      if (append.contains(Movie_.CREDITS)) {
        movie.credits(creditRepository.findAll(id));
      }
    }
    return movie;
  }
  
  @Transactional
  public Optional<Movie> findByTmdbId(@NotNull @Positive Integer id) {
    return repository.findByTmdbId(id)
        .map(m -> {
          m.credits(creditRepository.findAll(m.id()));
          return m;
        });
  }
  
  @Transactional
  public Movie update(Long id, UpdateMovie command) {
    long start = System.nanoTime();
    var existingMovie = findById(id, "credits");
    var newMovie = mapper.toMovie(command);
    newMovie.id(existingMovie.id());
    newMovie.tmdbId(existingMovie.tmdbId());
    var updatedMovie = repository.update(newMovie);
    
    List<UpdateMovieCredit> createCommands = new ArrayList<>();
    Map<UUID, UpdateMovieCredit> updateCommands = new HashMap<>();
    for (var cmd : command.credits()) {
      if (cmd.id() == null) {
        createCommands.add(cmd);
      } else {
        updateCommands.put(cmd.id(), cmd);
      }
    }
    updateCredits(existingMovie.credits(), updateCommands);
    insertCredits(updatedMovie, createCommands);
    var et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    LOGGER.infof("Updated %s in %d ms", updatedMovie, et);
    return updatedMovie;
  }
  
  @Transactional
  public void deleteById(Long id) {
    long start = System.nanoTime();
    var movie = findById(id, "credits");
    movie.backdrop().ifPresent(imageService::delete);
    movie.poster().ifPresent(imageService::delete);
    creditRepository.deleteAll(movie.credits());
    repository.deleteById(id);
    var et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    LOGGER.infof("Deleted %s in %d ms", movie, et);    
  }
  
  private void deleteImages(SaveMovie command, List<SaveMovieCredit> credits) {
    var oldPeople = credits.stream()
        .map(SaveMovieCredit::person)
        .collect(Collectors.toMap(SavePerson::tmdbId, Function.identity(), (p1, _) -> p1));
    for (var credit : command.credits()) {
      var newPerson = credit.person();
      var oldPerson = oldPeople.get(newPerson.tmdbId());
      if (oldPerson != null && !Objects.equals(oldPerson.tmdbProfile(), newPerson.tmdbProfile())) {
        if (oldPerson.profile() != null) {
          imageService.delete(oldPerson.profile());
        }
      }
    }    
  }
  
  private void insertCredits(Movie movie, List<UpdateMovieCredit> commands) {
    if (!commands.isEmpty()) {
      Set<Long> personIds = commands.stream()
          .map(UpdateMovieCredit::personId)
          .collect(Collectors.toSet());
      var people = personService.findByIdIn(List.copyOf(personIds)).stream()
          .collect(Collectors.toMap(Person::id, Function.identity()));
      List<MovieCredit> creditsToInsert = new ArrayList<>();
      for (var cmd : commands) {
        var person = people.get(cmd.personId());
        if (person == null) {
          throw new ResourceNotFoundException("Person not found: " + cmd.personId());
        }
        creditsToInsert.add(creditMapper.toMovieCredit(cmd, movie, person));
      }
      
      if (!creditsToInsert.isEmpty()) {
        creditRepository.insertAll(creditsToInsert);
      }     
    }    
  }
  
  private boolean isUpdatable(MovieCredit credit, UpdateMovieCredit command) {
    boolean result = false;
    if (command != null) {
      if (!Objects.equals(credit.role(), command.role())) {
        credit.role(command.role());
        result = true;
      }
      if (!Objects.equals(credit.order().orElse(null), command.order())) {
        credit.order(command.order());
        result = true;
      }        
    }
    return result;
  }
  
  private void saveCredits(Movie movie, Map<Integer, Person> people, List<SaveMovieCredit> commands) {
    List<MovieCredit> creditsToInsert = new ArrayList<>();
    List<MovieCredit> creditsToUpdate = new ArrayList<>();
    var existingCredits = creditRepository.findAll(movie.id()).stream()
        .collect(Collectors.toMap(c -> c.person().tmdbId(), Function.identity()));
    
    for (var credit : commands) {
      var person = Optional.ofNullable(people.get(credit.person().tmdbId()))
          .orElseThrow(() -> new ResourceNotFoundException("Person not found: " + credit.person().tmdbId())); 
      var newCredit = creditMapper.toMovieCredit(credit, movie, person);
      var existingCredit = existingCredits.remove(credit.person().tmdbId());
      if (existingCredit == null) {
        creditsToInsert.add(newCredit);
      } else if (!existingCredit.isEqualTo(newCredit)) {
        newCredit.id(existingCredit.id());
        creditsToUpdate.add(newCredit);
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
  }
  
  private void updateCredits(List<MovieCredit> credits, Map<UUID, UpdateMovieCredit> commands) {
    List<MovieCredit> creditsToDelete = new ArrayList<>();
    List<MovieCredit> creditsToUpdate = new ArrayList<>();
    for (var credit : credits) {
      var cmd = commands.get(credit.id());
      if (cmd != null) {
        if (isUpdatable(credit, cmd)) {
          creditsToUpdate.add(credit);
        }        
      } else {
        creditsToDelete.add(credit);
      }
    }
    if (!creditsToDelete.isEmpty()) {
      creditRepository.deleteAll(creditsToDelete); 
    }
    if (!creditsToUpdate.isEmpty()) {
      creditRepository.updateAll(creditsToUpdate);
    }    
  }
  
}
