package com.erdouglass.emdb.media.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.SaveMovieCommand;
import com.erdouglass.emdb.media.SaveMovieUseCase;
import com.erdouglass.emdb.media.SaveResult;
import com.erdouglass.emdb.media.SaveResult.Status;
import com.erdouglass.emdb.media.TmdbId;
import com.erdouglass.emdb.media.application.port.inbound.movie.DeleteMovieUseCase;
import com.erdouglass.emdb.media.application.port.inbound.movie.LockMovieCommand;
import com.erdouglass.emdb.media.application.port.inbound.movie.LockMovieUseCase;
import com.erdouglass.emdb.media.application.port.inbound.movie.UpdateMovieCommand;
import com.erdouglass.emdb.media.application.port.inbound.movie.UpdateMovieUseCase;
import com.erdouglass.emdb.media.application.port.outbound.movie.MovieAuditRepository;
import com.erdouglass.emdb.media.application.port.outbound.movie.MovieCommandRepository;
import com.erdouglass.emdb.media.domain.exception.MovieNotFoundException;
import com.erdouglass.emdb.media.domain.movie.Movie;
import com.erdouglass.emdb.media.domain.movie.MovieDetails;
import com.erdouglass.emdb.media.domain.movie.MovieId;
import com.erdouglass.emdb.media.domain.movie.MoviePublicId;
import com.erdouglass.emdb.media.domain.shared.Version;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

@ApplicationScoped
class MovieCommandService implements SaveMovieUseCase, UpdateMovieUseCase, LockMovieUseCase, DeleteMovieUseCase {
  private static final TimeBasedEpochGenerator GENERATOR = Generators.timeBasedEpochGenerator();
  private static final Logger LOGGER = Logger.getLogger(MovieCommandService.class);
  
  @Inject
  MovieAuditRepository audit;
  
  @Inject
  MovieCommandRepository movies;

  @Override
  @Transactional
  public SaveResult save(SaveMovieCommand command) {
    var details = MovieMapper.toMovieDetails(command);
    return movies.findByTmdbId(command.tmdbId())
        .map(existing -> update(existing, details))
        .orElseGet(() -> insert(command.tmdbId(), details));
  }
  
  @Override
  @Transactional
  public SaveResult update(UpdateMovieCommand command) {
    var details = MovieMapper.toMovieDetails(command);
    Movie existing = movies.findByPublicId(MoviePublicId.of(command.publicId()))
        .orElseThrow(() -> new MovieNotFoundException(command.publicId()));
    existing.checkVersion(Version.of(command.version()));
    return update(existing, details);
  }
  
  @Override
  @Transactional
  public SaveResult lock(LockMovieCommand command) {
    Movie movie = movies.findByPublicId(command.publicId())
        .orElseThrow(() -> new MovieNotFoundException(command.publicId().value()));
    movie.checkVersion(command.version());
    movie.lock(command.lock());
    var updated = movies.update(movie);
    LOGGER.infof("Updated: %s", updated);
    return SaveResult.of(
        updated.publicId().map(MoviePublicId::value).orElseThrow(), 
        updated.version().map(Version::value).orElseThrow(), 
        Status.UPDATED);
  }
  
  @Override
  @Transactional
  public void delete(MoviePublicId id) {
    Movie movie = movies.findByPublicId(id)
        .orElseThrow(() -> new MovieNotFoundException(id.value())); 
    audit.append(movie.id(), movie.publicId().orElseThrow(), movie.changesAsDeleted());
    movies.deleteByPublicId(id);
  }
  
  private SaveResult insert(TmdbId tmdbId, MovieDetails details) {
    var movie = Movie.create(MovieId.of(GENERATOR.generate()), tmdbId, details);
    var inserted = movies.insert(movie);
    audit.append(inserted.id(), inserted.publicId().orElseThrow(), inserted.changesAsAdded());
    LOGGER.infof("Created: %s", inserted);
    return SaveResult.of(
        inserted.publicId().map(MoviePublicId::value).orElseThrow(), 
        inserted.version().map(Version::value).orElseThrow(), 
        Status.CREATED);    
  }
  
  private SaveResult update(Movie movie, MovieDetails target) {
    var changes = movie.update(target);
    if (changes.isEmpty()) {
      return SaveResult.of(
          movie.publicId().map(MoviePublicId::value).orElseThrow(), 
          movie.version().map(Version::value).orElseThrow(), 
          Status.UNCHANGED);
    }
    var updated = movies.update(movie);
    audit.append(updated.id(), updated.publicId().orElseThrow(), changes);
    LOGGER.infof("Updated: %s", updated);
    return SaveResult.of(
        updated.publicId().map(MoviePublicId::value).orElseThrow(), 
        updated.version().map(Version::value).orElseThrow(), 
        Status.UPDATED);    
  }
}
