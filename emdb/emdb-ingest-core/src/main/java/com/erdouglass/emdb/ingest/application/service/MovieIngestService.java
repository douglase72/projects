package com.erdouglass.emdb.ingest.application.service;

import java.math.BigDecimal;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import com.erdouglass.common.util.DateTimeFactory;
import com.erdouglass.emdb.ingest.application.port.out.Media;
import com.erdouglass.emdb.ingest.application.port.out.Movie;
import com.erdouglass.emdb.ingest.domain.model.TmdbId;

@ApplicationScoped
class MovieIngestService {
  private static final Logger LOGGER = Logger.getLogger(MovieIngestService.class);

  @Inject
  Media media;
  
  public void ingest() {
    var movie = Movie.builder()
        .tmdbId(TmdbId.of(78))
        .title("Blade Runner")
        .releaseDate(DateTimeFactory.from("1982-06-25"))
        .score(BigDecimal.valueOf(8.983))
        .originalLanguage("en")
        .overview("Test overview.")
        .build();
    media.save(movie);
    LOGGER.infof("Loaded: %s", movie);    
  }
}
