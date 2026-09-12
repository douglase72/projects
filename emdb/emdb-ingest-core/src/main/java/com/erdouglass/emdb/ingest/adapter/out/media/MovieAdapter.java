package com.erdouglass.emdb.ingest.adapter.out.media;

import java.math.BigDecimal;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.common.util.DateTimeFactory;
import com.erdouglass.emdb.common.TmdbId;
import com.erdouglass.emdb.ingest.application.port.out.MovieRepository;
import com.erdouglass.emdb.media.SaveMovieCommand;
import com.erdouglass.emdb.media.SaveMovieUseCase;

@ApplicationScoped
class MovieAdapter implements MovieRepository {
  
  @Inject
  SaveMovieUseCase saveUseCase;

  @Override
  public void save() {
    var command = SaveMovieCommand.builder()
        .tmdbId(TmdbId.of(78))
        .title("Blade Runner")
        .releaseDate(DateTimeFactory.from("1982-06-25"))
        .score(BigDecimal.valueOf(7.893))
        .originalLanguage("en")
        .overview("In the smog-choked dystopian Los Angeles of 2019, blade runner Rick Deckard is called out of retirement to terminate a quartet of replicants who have escaped to Earth seeking their creator for a way to extend their short life spans.")
        .build();
    saveUseCase.save(command);
  }
}
