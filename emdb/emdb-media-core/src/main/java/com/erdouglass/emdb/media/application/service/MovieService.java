package com.erdouglass.emdb.media.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.SaveMovieCommand;
import com.erdouglass.emdb.media.SaveMovieUseCase;
import com.erdouglass.emdb.media.SaveResult;
import com.erdouglass.emdb.media.SaveResult.Status;
import com.erdouglass.emdb.media.application.port.outbound.MovieRepository;
import com.erdouglass.emdb.media.domain.movie.Movie;
import com.erdouglass.emdb.media.domain.movie.MovieId;
import com.erdouglass.emdb.media.domain.movie.ReleaseDate;
import com.erdouglass.emdb.media.domain.shared.OriginalLanguage;
import com.erdouglass.emdb.media.domain.shared.SourceId;
import com.erdouglass.emdb.media.domain.shared.SourceId.Source;
import com.erdouglass.emdb.media.domain.shared.Title;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

/// Application service implementing the write-side movie use cases.
///
/// The inside edge of the hexagon: this class *orchestrates* — mint the
/// surrogate identity, assemble value objects from the raw command (which is
/// where invariants fire), delegate persistence through the outbound port,
/// shape the result — but it owns no business rules. If an `if` in here
/// starts encoding movie behavior, it belongs in the domain model.
///
/// This is also the transaction boundary: `@Transactional` lives on the
/// use-case method and nowhere else, so one command is one atomic unit
/// regardless of how many outbound ports it touches.
@ApplicationScoped
class MovieService implements SaveMovieUseCase {
  private static final TimeBasedEpochGenerator GENERATOR = Generators.timeBasedEpochGenerator();
  private static final Logger LOGGER = Logger.getLogger(MovieService.class);
  
  @Inject
  MovieRepository repository;
  
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
    var existing = repository.findBySourceId(command.source(), command.sourceId()).orElse(null);
    if (existing == null) {
      movie = repository.insert(movie);
    } else {
      existing.merge(command);
      movie = repository.update(existing);
      status = Status.UPDATED;
    }
    LOGGER.infof("Saved: %s", movie);
    return new SaveResult(movie.publicId().toString(), status);
  }
}
