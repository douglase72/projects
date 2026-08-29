package com.erdouglass.emdb.media.movie.application.service;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.kernel.Result;
import com.erdouglass.emdb.media.kernel.Result.Status;
import com.erdouglass.emdb.media.movie.application.port.in.CreditSpec;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieCommand;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieUseCase;
import com.erdouglass.emdb.media.movie.application.port.out.MovieCommandRepository;
import com.erdouglass.emdb.media.movie.domain.model.CreditDetails;
import com.erdouglass.emdb.media.movie.domain.model.Movie;
import com.erdouglass.emdb.media.movie.domain.model.MoviePublicId;
import com.erdouglass.emdb.media.person.application.port.out.PersonDirectory;
import com.erdouglass.emdb.media.person.application.port.out.PersonStub;

@ApplicationScoped
class MovieCommandService implements SaveMovieUseCase {
  private static final Logger LOGGER = Logger.getLogger(MovieCommandService.class);
  
  @Inject
  MovieCommandRepository movies;
  
  @Inject
  PersonDirectory people;

  /// Save the movie described by the command.
  /// 
  /// Create the movie if it does not already exist, otherwise update the 
  /// existing movie.
  /// 
  /// @param command the command that describes the [Movie]
  /// @return the result
  @Override
  @Transactional
  public Result save(SaveMovieCommand command) {
    return movies.findByTmdbId(command.tmdbId())
        .map(existing -> update(existing, command))
        .orElseGet(() -> insert(command));
  }
  
  private Result insert(SaveMovieCommand command) {
    var movie = Movie.create(command.tmdbId(), command.details(), resolve(command.credits()));
    var inserted = movies.insert(movie);
    LOGGER.infof("Saved: %s", inserted);
    return Result.of(
        inserted.publicId().map(MoviePublicId::value).orElseThrow(),
        inserted.version().value(), 
        Status.CREATED);
  }
  
  private Result update(Movie existing, SaveMovieCommand command) {
    existing.update(command.details(), resolve(command.credits()));
    movies.update(existing);
    LOGGER.infof("Saved: %s", existing);
    return Result.of(
        existing.publicId().map(MoviePublicId::value).orElseThrow(),
        existing.version().value(), 
        Status.UPDATED);
  }
  
  private List<CreditDetails> resolve(List<CreditSpec> specs) {
    var stubs = people.register(specs.stream()
        .map(c -> PersonStub.of(c.personId(), c.name()))
        .collect(Collectors.toSet()));
    return specs.stream()
        .map(s -> s.toDetails(stubs.get(s.personId())))
        .toList();
  }
}
