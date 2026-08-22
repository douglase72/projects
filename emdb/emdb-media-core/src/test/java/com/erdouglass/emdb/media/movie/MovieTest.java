package com.erdouglass.emdb.media.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import com.erdouglass.emdb.media.SourceId;
import com.erdouglass.emdb.media.SourceId.Source;
import com.erdouglass.emdb.media.kernel.LanguageCode;
import com.erdouglass.emdb.media.kernel.Overview;
import com.erdouglass.emdb.media.kernel.Score;
import com.erdouglass.emdb.media.kernel.Title;
import com.erdouglass.emdb.media.movie.domain.Movie;
import com.erdouglass.emdb.media.movie.domain.MovieDetails;
import com.erdouglass.emdb.media.movie.domain.MovieId;
import com.erdouglass.emdb.media.movie.domain.ReleaseDate;

class MovieTest {
  private static final UUID ID = UUID.fromString("01a00bcf-1e3a-76b6-980c-f08e90e21a95");
  private static final Logger LOGGER = Logger.getLogger(MovieTest.class);
  
  @Test
  void testCreateCompleteMovie() {
    var details = new MovieDetails(
        Title.of("Blade Runner"),
        Optional.of(ReleaseDate.from("1982-06-25")),
        Optional.of(Score.of(BigDecimal.valueOf(7.893))),
        Optional.of(LanguageCode.of("en")),
        Optional.of(Overview.of("In the smog-choked dystopian Los Angeles of 2019, blade runner Rick Deckard is called out of retirement to terminate a quartet of replicants who have escaped to Earth seeking their creator for a way to extend their short life spans.")));
    var movie = Movie.create(MovieId.of(ID), SourceId.of(Source.TMDB, "78"), details);
    assertEquals("01a00bcf-1e3a-76b6-980c-f08e90e21a95", movie.id().value().toString());
    assertEquals("Blade Runner", movie.details().title().value());
    assertEquals("1982-06-25", movie.details().releaseDate().map(r -> r.value().toDateString()).orElseThrow());
    assertEquals(7.893, movie.details().score().map(s -> s.value().doubleValue()).orElseThrow(), 0.001);
    assertEquals("en", movie.details().originalLanguage().map(LanguageCode::value).orElseThrow());
    assertEquals("In the smog-choked dystopian Los Angeles of 2019, blade runner Rick Deckard is called out of retirement to terminate a quartet of replicants who have escaped to Earth seeking their creator for a way to extend their short life spans.", movie.details().overview().map(Overview::value).orElseThrow());
    LOGGER.info(movie);
  }
  
  @Test
  void testCreateWithoutReleaseDate() {
    var details = new MovieDetails(
        Title.of("Blade Runner"),
        Optional.empty(),
        Optional.of(Score.of(BigDecimal.valueOf(7.893))),
        Optional.of(LanguageCode.of("en")),
        Optional.of(Overview.of("In the smog-choked dystopian Los Angeles of 2019, blade runner Rick Deckard is called out of retirement to terminate a quartet of replicants who have escaped to Earth seeking their creator for a way to extend their short life spans.")));
    var movie = Movie.create(MovieId.of(ID), SourceId.of(Source.TMDB, "78"), details);
    assertEquals("Blade Runner", movie.details().title().value());
    assertTrue(movie.details().releaseDate().isEmpty());
    assertEquals(7.893, movie.details().score().map(s -> s.value().doubleValue()).orElseThrow(), 0.001);
    assertEquals("en", movie.details().originalLanguage().map(LanguageCode::value).orElseThrow());
    assertEquals("In the smog-choked dystopian Los Angeles of 2019, blade runner Rick Deckard is called out of retirement to terminate a quartet of replicants who have escaped to Earth seeking their creator for a way to extend their short life spans.", movie.details().overview().map(Overview::value).orElseThrow());
    LOGGER.info(movie);
  }
}
