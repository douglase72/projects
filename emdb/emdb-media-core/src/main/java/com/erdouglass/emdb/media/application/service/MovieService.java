package com.erdouglass.emdb.media.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.SaveMovieCommand;
import com.erdouglass.emdb.media.SaveMovieUseCase;
import com.erdouglass.emdb.media.SaveResult;
import com.erdouglass.emdb.media.application.port.outbound.MovieRepositoryPort;
import com.erdouglass.emdb.media.domain.movie.Movie;
import com.erdouglass.emdb.media.domain.movie.MovieId;
import com.erdouglass.emdb.media.domain.movie.ReleaseDate;
import com.erdouglass.emdb.media.domain.movie.Title;
import com.erdouglass.emdb.media.domain.shared.SourceId;
import com.erdouglass.emdb.media.domain.shared.SourceId.Source;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

@ApplicationScoped
class MovieService implements SaveMovieUseCase {
  private static final TimeBasedEpochGenerator GENERATOR = Generators.timeBasedEpochGenerator();
  private static final Logger LOGGER = Logger.getLogger(MovieService.class);
  
  @Inject
  MovieRepositoryPort repository;
  
  @Override
  @Transactional
  public SaveResult save(SaveMovieCommand command) {
    Movie movie = Movie.builder()
        .id(new MovieId(GENERATOR.generate()))
        .sourceId(new SourceId(Source.from(command.sourceId().source()), command.sourceId().id()))
        .title(new Title(command.title()))
        .releaseDate(new ReleaseDate(command.releaseDate()))
        .build();
    Movie saved = repository.save(movie);
    LOGGER.infof("Saved: %s", saved);
    SaveResult result = new SaveResult(saved.publicId().toString(), saved.saveStatus());
    return result;
  }
}
