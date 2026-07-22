package com.erdouglass.emdb.media.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.SaveMovieCommand;
import com.erdouglass.emdb.media.SaveMovieUseCase;
import com.erdouglass.emdb.media.SaveResult;
import com.erdouglass.emdb.media.SaveResult.Status;
import com.erdouglass.emdb.media.SourceId;
import com.erdouglass.emdb.media.application.port.inbound.UpdateMovieCommand;
import com.erdouglass.emdb.media.application.port.inbound.UpdateMovieUseCase;
import com.erdouglass.emdb.media.application.port.inbound.UpdateResult;
import com.erdouglass.emdb.media.application.port.outbound.MovieRepository;
import com.erdouglass.emdb.media.domain.exception.MovieNotFoundException;
import com.erdouglass.emdb.media.domain.movie.Movie;
import com.erdouglass.emdb.media.domain.movie.MovieId;
import com.erdouglass.emdb.media.domain.movie.MoviePublicId;
import com.erdouglass.emdb.media.domain.shared.Version;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

/// Application service implementing the write-side movie use cases.
///
/// The inside edge of the hexagon: this class *orchestrates* — mint
/// surrogate identity, assemble value objects from raw command fields,
/// delegate persistence through the outbound port, shape results — but owns
/// no business rules; if an `if` here starts encoding movie behavior, it
/// belongs in the domain model.
///
/// Also the transaction boundary: `@Transactional` lives on the use-case
/// methods and nowhere else, so one command is one atomic unit.
@ApplicationScoped
class MovieService implements SaveMovieUseCase, UpdateMovieUseCase {
  private static final TimeBasedEpochGenerator GENERATOR = Generators.timeBasedEpochGenerator();
  private static final Logger LOGGER = Logger.getLogger(MovieService.class);
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  MovieRepository repository;

  /// Upsert the given movie to the database.
  /// 
  /// This method is idempotent with respect to the [SourceId], meaning if a [Movie]
  /// already exists with the same source id it will be updated, otherwise a new movie
  /// will be created.
  /// 
  /// @param command - the command holding the movie to be saved
  /// @return the result of the save
  @Override
  @Transactional
  public SaveResult save(SaveMovieCommand command) {
    var movie = Movie.builder()
        .id(MovieId.of(GENERATOR.generate()))
        .sourceId(command.sourceId())
        .title(command.title())
        .releaseDate(command.releaseDate())
        .originalLanguage(command.originalLanguage())
        .build();
    var status = Status.CREATED;
    var existing = repository.findBySourceId(command.sourceId())
        .orElse(null);
    if (existing == null) {
      movie = repository.insert(movie);
    } else {
      movie = repository.update(mapper.merge(existing, command));
      status = Status.UPDATED;
    }
    LOGGER.infof("Saved: %s", movie);
    return new SaveResult(
        movie.publicId().map(MoviePublicId::toString).orElseThrow(), 
        movie.version().map(Version::value).orElseThrow(), 
        status);
  }

  @Override
  @Transactional
  public UpdateResult update(String id, UpdateMovieCommand command) {
    var existing = repository.findByPublicId(MoviePublicId.from(id))
        .orElseThrow(() -> new MovieNotFoundException(id));    
    var updated = repository.update(mapper.merge(existing, command));
    LOGGER.infof("Updated: %s", updated);
    return new UpdateResult(
        updated.publicId().map(MoviePublicId::toString).orElseThrow(), 
        updated.version().map(Version::value).orElseThrow());
  }
}
