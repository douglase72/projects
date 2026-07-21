package com.erdouglass.emdb.media.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.SaveMovieCommand;
import com.erdouglass.emdb.media.SaveMovieUseCase;
import com.erdouglass.emdb.media.SaveResult;
import com.erdouglass.emdb.media.SaveResult.Status;
import com.erdouglass.emdb.media.application.port.inbound.UpdateMovieUseCase;
import com.erdouglass.emdb.media.application.port.inbound.UpdateResult;
import com.erdouglass.emdb.media.application.port.outbound.MovieRepository;
import com.erdouglass.emdb.media.domain.exception.MovieNotFoundException;
import com.erdouglass.emdb.media.domain.movie.Movie;
import com.erdouglass.emdb.media.domain.movie.MovieId;
import com.erdouglass.emdb.media.domain.movie.MoviePublicId;
import com.erdouglass.emdb.media.domain.movie.ReleaseDate;
import com.erdouglass.emdb.media.domain.movie.UpdateMovie;
import com.erdouglass.emdb.media.domain.shared.OriginalLanguage;
import com.erdouglass.emdb.media.domain.shared.SourceId;
import com.erdouglass.emdb.media.domain.shared.SourceId.Source;
import com.erdouglass.emdb.media.domain.shared.Title;
import com.erdouglass.emdb.media.domain.shared.Version;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

@ApplicationScoped
class MovieService implements SaveMovieUseCase, UpdateMovieUseCase {
  private static final TimeBasedEpochGenerator GENERATOR = Generators.timeBasedEpochGenerator();
  private static final Logger LOGGER = Logger.getLogger(MovieService.class);
  
  @Inject
  MovieRepository repository;
  
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

  /// Upsert keyed by external provenance: probe by source identity, insert
  /// on miss, merge-and-update on hit. The `uq_movie_source` constraint
  /// backstops the probe-then-insert race — two concurrent first saves
  /// resolve to one row and one constraint violation, never duplicates.
  @Override
  @Transactional
  public SaveResult save(SaveMovieCommand command) {
    var movie = Movie.builder()
        .id(MovieId.of(GENERATOR.generate()))
        .sourceId(SourceId.of(Source.from(command.source()), command.sourceId()))
        .title(Title.of(command.title()))
        .releaseDate(ReleaseDate.of(command.releaseDate()))
        .originalLanguage(OriginalLanguage.of(command.originalLanguage()))
        .build();
    var status = Status.CREATED;
    var existing = repository.findBySourceId(SourceId.of(Source.from(command.source()), command.sourceId()))
        .orElse(null);
    if (existing == null) {
      movie = repository.insert(movie);
    } else {
      existing.merge(command);
      movie = repository.update(existing);
      status = Status.UPDATED;
    }
    LOGGER.infof("Saved: %s", movie);
    return new SaveResult(
        movie.publicId().map(MoviePublicId::toString).orElseThrow(), 
        movie.version().map(Version::value).orElseThrow(), 
        status);
  }

  /// Load by public id, stamp the claimed version via
  /// [Movie#merge(UpdateMovie)], write through the version guard. Returns
  /// facts from the *updated* aggregate — returning `existing`'s version
  /// would hand back a pre-bump number and manufacture a phantom conflict
  /// on the client's very next edit.
  @Override
  @Transactional
  public UpdateResult update(String id, UpdateMovie command) {
    var existing = repository.findByPublicId(MoviePublicId.from(id))
        .orElseThrow(() -> new MovieNotFoundException(id));
    existing.merge(command);
    var updated = repository.update(existing);
    LOGGER.infof("Updated: %s", updated);
    return new UpdateResult(
        updated.publicId().map(MoviePublicId::toString).orElseThrow(), 
        updated.version().map(Version::value).orElseThrow());
  }
}
