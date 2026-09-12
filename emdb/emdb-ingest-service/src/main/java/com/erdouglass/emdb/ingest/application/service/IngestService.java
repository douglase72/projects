package com.erdouglass.emdb.ingest.application.service;

import java.math.BigDecimal;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.application.port.in.IngestMediaCommand;
import com.erdouglass.emdb.ingest.application.port.in.IngestMediaUseCase;
import com.erdouglass.emdb.ingest.application.port.out.Movie;
import com.erdouglass.emdb.ingest.application.port.out.MovieRepository;
import com.erdouglass.emdb.ingest.domain.model.Ingest;
import com.erdouglass.emdb.ingest.domain.model.IngestId;

@ApplicationScoped
class IngestService implements IngestMediaUseCase {
  private static final Logger LOGGER = Logger.getLogger(IngestService.class);
  
  @Inject
  MovieRepository movies;

  @Override
  public IngestId ingest(IngestMediaCommand command) {
    var ingest = Ingest.submit(command.tmdbId(), command.ingestType());
    LOGGER.infof("ingest: %s", ingest);
    var movie = Movie.builder()
        .tmdbId(78)
        .title("Blade Runner")
        .releaseDate("1982-06-25")
        .score(BigDecimal.valueOf(7.893))
        .originalLanguage("en")
        .overview("In the smog-choked dystopian Los Angeles of 2019, blade runner Rick Deckard is called out of retirement to terminate a quartet of replicants who have escaped to Earth seeking their creator for a way to extend their short life spans.")        
        .build();
    movies.save(ingest.id(), movie);
    return ingest.id();
  }
}
