package com.erdouglass.emdb.media.service;

import java.util.Objects;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.common.comand.UpdateMovie;
import com.erdouglass.emdb.media.entity.Movie;
import com.erdouglass.emdb.media.mapper.MovieMapper;
import com.erdouglass.emdb.media.repository.MovieRepository;
import com.erdouglass.emdb.scraper.service.TmdbMovieScraper;
import com.erdouglass.webservices.ResourceNotFoundException;

import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class MovieService extends MediaService {
  private static final Logger LOGGER = Logger.getLogger(MovieService.class);
  private static final String ROUTE_KEY = "movie.invalid";
  
  @Inject
  @Channel("movie-dlq-out")
  Emitter<SaveMovie> dlqEmitter;
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  PersonService personService;
  
  @Inject
  TmdbMovieScraper scraper;
  
  @Inject
  MovieRepository repository;
  
  @Override
  public void ingest(@NotNull @Positive Integer tmdbId, String jobId) {
    var existingMovie = findByTmdbId(tmdbId);
    var command = existingMovie
        .map(mapper::toSaveMovie)
        .orElseGet(() -> SaveMovie.builder().tmdbId(tmdbId).build());
    var saveCommand = scraper.scrape(command, jobId);
    
    try {
      validate(saveCommand);
      var movie = save(saveCommand);
      LOGGER.infof("Saved: %s", movie);
      existingMovie.ifPresent(m -> {
        if (!Objects.equals(m.tmdbBackdrop().orElse(null), movie.tmdbBackdrop().orElse(null))) {
          m.backdrop().ifPresent(imageService::delete);
        }
        if (!Objects.equals(m.tmdbPoster().orElse(null), movie.tmdbPoster().orElse(null))) {
          m.poster().ifPresent(imageService::delete);
        }      
      });
      
      // TODO: Delete each persons old image
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
    var movie = mapper.toMovie(command);
    repository.findByTmdbId(movie.tmdbId()).ifPresent(m -> movie.id(m.id()));
    var savedMovie = repository.save(movie);
    personService.saveAll(command.people());
    return savedMovie; 
  }
  
  @Transactional
  public Optional<Movie> findById(@NotNull @Positive Long id, String append) {
    return repository.findById(id);
  }
  
  @Transactional
  public Optional<Movie> findByTmdbId(@NotNull @Positive Integer id) {
    return repository.findByTmdbId(id);
  }
  
  @Transactional
  public Movie update(Long id, UpdateMovie command) {
    var existingMovie = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Movie not found with Id: " + id));
    var movie = mapper.toMovie(command);
    movie.id(existingMovie.id());
    movie.tmdbId(existingMovie.tmdbId());
    var updatedMovie = repository.update(movie);
    return updatedMovie;
  }
  
  @Transactional
  public void deleteById(Long id) {
    var movie = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + id));
    repository.deleteById(id);
    LOGGER.infof("Deleted: %s", movie);
  }    
  
}
