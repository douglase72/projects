package com.erdouglass.emdb.media.movie.application.service;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.kernel.AggregateId;
import com.erdouglass.emdb.media.kernel.Credit;
import com.erdouglass.emdb.media.kernel.Credit.CastDto;
import com.erdouglass.emdb.media.kernel.Credit.CrewDto;
import com.erdouglass.emdb.media.movie.application.port.in.CreditSpec;
import com.erdouglass.emdb.media.movie.application.port.in.Result;
import com.erdouglass.emdb.media.movie.application.port.in.CreditSpec.CastSpec;
import com.erdouglass.emdb.media.movie.application.port.in.CreditSpec.CrewSpec;
import com.erdouglass.emdb.media.movie.application.port.in.Result.Status;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieCommand;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieUseCase;
import com.erdouglass.emdb.media.movie.application.port.out.MovieCommandRepository;
import com.erdouglass.emdb.media.movie.application.port.out.PersonCredit;
import com.erdouglass.emdb.media.movie.application.port.out.ResolvePeople;
import com.erdouglass.emdb.media.movie.domain.model.Movie;

@ApplicationScoped
class MovieCommandService implements SaveMovieUseCase {
  private static final Logger LOGGER = Logger.getLogger(MovieCommandService.class);
  
  @Inject
  MovieCommandRepository movies;
  
  @Inject
  ResolvePeople people;

  @Override
  @Transactional
  public Result save(SaveMovieCommand command) {
    return movies.findByTmdbId(command.tmdbId())
        .map(existing -> update(existing, command))
        .orElseGet(() -> insert(command));
  }
  
  private Result insert(SaveMovieCommand command) {
    var movie = Movie.create(command.tmdbId(), command.details(), toCredits(command.credits()));
    var inserted = movies.insert(movie);
    LOGGER.debugf("Saved: %s", inserted);
    return Result.of(inserted.id(), inserted.version(), Status.CREATED);
  }
  
  private Result update(Movie existing, SaveMovieCommand command) {
    existing.update(command.details(), toCredits(command.credits()));
    var updated = movies.update(existing);
    LOGGER.debugf("Saved: %s", updated);
    return Result.of(updated.id(), updated.version(), Status.UPDATED);
  }
  
  private List<Credit> toCredits(List<CreditSpec> credits) {
    var personIds = people.resolve(credits.stream()
        .map(c -> PersonCredit.of(c.personId(), c.name()))
        .collect(Collectors.toSet())); 
    return credits.stream()
        .map(c -> toCredit(c, personIds.get(c.personId())))
        .toList();
  }
  
  private Credit toCredit(CreditSpec credit, AggregateId personId) {
    return switch (credit) {
      case CastSpec cast -> CastDto.builder()
          .tmdbId(cast.tmdbId())
          .personId(personId)
          .name(cast.name())
          .character(cast.character())
          .order(cast.order())
          .build();
      case CrewSpec crew -> CrewDto.builder()
        .tmdbId(crew.tmdbId())
        .personId(personId)
        .name(crew.name())
        .job(crew.job())
        .department(crew.department())
        .build();     
    };
  }
}
