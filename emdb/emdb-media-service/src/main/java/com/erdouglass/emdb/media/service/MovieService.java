package com.erdouglass.emdb.media.service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
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
import com.erdouglass.emdb.common.comand.SaveMovie.Credits;
import com.erdouglass.emdb.common.comand.UpdateMovie;
import com.erdouglass.emdb.common.comand.UpdateMovieCredit;
import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.common.event.IngestStatusChanged.IngestSource;
import com.erdouglass.emdb.common.event.IngestStatusChanged.IngestStatus;
import com.erdouglass.emdb.common.query.MovieDto;
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

import io.quarkus.narayana.jta.QuarkusTransaction;
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
  @ActivateRequestContext
  public Duration ingest(@NotNull @Positive Integer tmdbId, @NotNull UUID jobId) {
    var start = Instant.now();
    var existingMovie = repository.findByTmdbId(tmdbId);
    var command = existingMovie
        .map(mapper::toSaveMovie)
        .orElseGet(() -> SaveMovie.builder().tmdbId(tmdbId).build());
    var saveCommand = scraper.scrape(command, jobId);
    
    try {
      validate(saveCommand);
      var movie = QuarkusTransaction.requiringNew().call(() -> saveMovie(saveCommand));      
      existingMovie.ifPresent(m -> deleteShowImages(m, movie));
      deletePeopleImages(saveCommand.people(), command.people());
      var et = Duration.between(start, Instant.now());
      var msg = String.format("Ingest Job for TMDB %s %d completed in %d ms", 
          MediaType.MOVIE, movie.tmdbId(), et.toMillis());
      LOGGER.info(msg);
      statusService.send(IngestStatusChanged.builder()
          .id(jobId)
          .status(IngestStatus.COMPLETED)
          .tmdbId(tmdbId)
          .source(IngestSource.MEDIA)
          .type(MediaType.MOVIE)
          .name(movie.title())
          .emdbId(movie.id())
          .message(msg)
          .build());
      return et;
    } catch (Exception e) {
      dlqEmitter.send(Message.of(saveCommand)
          .addMetadata(OutgoingRabbitMQMetadata.builder()
          .withRoutingKey(ROUTE_KEY)
          .build()));
      throw new RuntimeException(e);
    }
  }
  
  @Transactional
  public MovieDto save(SaveMovie command) {
    long start = System.nanoTime();
    var movie = saveMovie(command);
    var et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    LOGGER.infof("Saved %s in %d ms", movie, et);
    return mapper.toMovieDto(movie);
  }
  
  @Transactional
  public MovieDto findById(Long id, String append) {
    long start = System.nanoTime();
    var movie = findMovieById(id, append);
    var et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    LOGGER.infof("Found %s in %d ms", movie, et);
    return mapper.toMovieDto(movie);
  }
  
  @Transactional
  public MovieDto update(Long id, UpdateMovie command) {
    long start = System.nanoTime();
    var existingMovie = findMovieById(id, "credits");
    var newMovie = mapper.toMovie(command);
    newMovie.id(existingMovie.id());
    newMovie.tmdbId(existingMovie.tmdbId());
    var updatedMovie = repository.update(newMovie);
    var et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    LOGGER.infof("Updated %s in %d ms", updatedMovie, et);
    return mapper.toMovieDto(updatedMovie);
  }
  
  @Transactional
  public void deleteById(Long id) {
    long start = System.nanoTime();
    var movie = findMovieById(id, "credits");
    movie.backdrop().ifPresent(imageService::delete);
    movie.poster().ifPresent(imageService::delete);
    creditRepository.deleteAll(movie.credits());
    repository.deleteById(id);
    var et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    LOGGER.infof("Deleted %s in %d ms", movie, et);    
  }
  
  @Transactional
  public void updateCredit(UUID id, UpdateMovieCredit command) {
    var credit = creditRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No credit found with id: " + id));
    credit.role(command.role());
    credit.order(command.order());
    creditRepository.update(credit);
  }
  
  private Movie saveMovie(SaveMovie command) {
    var movie = mapper.toMovie(command);
    repository.findByTmdbId(movie.tmdbId()).ifPresent(m -> movie.id(m.id()));
    var savedMovie = repository.save(movie);
    var savedPeople = personService.saveAll(command.people()).stream()
        .collect(Collectors.toMap(s -> s.person().tmdbId(), s -> s.person()));
    saveCredits(savedMovie, savedPeople, command.credits());
    return savedMovie;
  }
  
  private Movie findMovieById(Long id, String append) {
    var movie = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + id));
    movie.credits(List.of());
    if (append != null && append.contains(Movie_.CREDITS)) {
      movie.credits(creditRepository.findByMovieId(id));
    }
    return movie;    
  }
  
  private void saveCredits(Movie movie, Map<Integer, Person> people, Credits credits) {
    List<MovieCredit> creditsToInsert = new ArrayList<>();
    List<MovieCredit> creditsToUpdate = new ArrayList<>();
    var existingCredits = creditRepository.findByMovieId(movie.id()).stream()
        .collect(Collectors.toMap(
            c -> c.person().tmdbId() + "-" + c.type(), 
            Function.identity(), 
            (existing, _) -> existing));
    
    List<MovieCredit> mappedCredits = new ArrayList<>(); 
    for (var cmd : credits.cast()) {
      var person = Optional.ofNullable(people.get(cmd.person().tmdbId()))
          .orElseThrow(() -> new ResourceNotFoundException("Person not found: " + cmd.person().tmdbId())); 
      mappedCredits.add(creditMapper.toMovieCredit(cmd, movie, person));      
    }
    for (var cmd : credits.crew()) {
      var person = Optional.ofNullable(people.get(cmd.person().tmdbId()))
          .orElseThrow(() -> new ResourceNotFoundException("Person not found: " + cmd.person().tmdbId())); 
      mappedCredits.add(creditMapper.toMovieCredit(cmd, movie, person));
    }
    
    for (var newCredit : mappedCredits) {
      var key = newCredit.person().tmdbId() + "-" + newCredit.type();
      var existingCredit = existingCredits.remove(key);
      if (existingCredit == null) {
        creditsToInsert.add(newCredit);
      } else if (!existingCredit.isEqualTo(newCredit)) {
        existingCredit.role(newCredit.role()); 
        existingCredit.order(newCredit.order().orElse(null));
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
  }
  
}
