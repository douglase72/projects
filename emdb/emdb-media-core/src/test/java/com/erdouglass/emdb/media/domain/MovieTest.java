package com.erdouglass.emdb.media.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import com.erdouglass.emdb.media.TmdbId;
import com.erdouglass.emdb.media.domain.movie.Movie;
import com.erdouglass.emdb.media.domain.movie.MovieDetails;
import com.erdouglass.emdb.media.domain.movie.MovieId;
import com.erdouglass.emdb.media.domain.movie.ReleaseDate;
import com.erdouglass.emdb.media.domain.movie.Title;
import com.erdouglass.emdb.media.domain.shared.LanguageCode;
import com.erdouglass.emdb.media.domain.shared.Overview;
import com.erdouglass.emdb.media.domain.shared.Score;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

class MovieTest {
  private static final TimeBasedEpochGenerator GENERATOR = Generators.timeBasedEpochGenerator();
  private static final Logger LOGGER = Logger.getLogger(MovieTest.class);
  
  @Test
  void testCreateCompleteMovie() {
    var details = MovieDetails.builder()
        .title(Title.of("Blade Runner"))
        .releaseDate(ReleaseDate.from("1982-06-25"))
        .score(Score.of(BigDecimal.valueOf(7.893)))
        .originalLanguage(LanguageCode.of("en"))
        .overview(Overview.of("In the smog-choked dystopian Los Angeles of 2019, blade runner Rick Deckard is called out of retirement to terminate a quartet of replicants who have escaped to Earth seeking their creator for a way to extend their short life spans."))
        .build();
    var movie = Movie.create(MovieId.of(GENERATOR.generate()), TmdbId.of(78), details);
    assertEquals("Blade Runner", movie.details().title().value());
    assertEquals("1982-06-25", movie.details().releaseDate().map(r -> r.value().toDateString()).orElseThrow());
    assertEquals(7.893, movie.details().score().map(s -> s.value().doubleValue()).orElseThrow(), 0.001);
    assertEquals("en", movie.details().originalLanguage().map(LanguageCode::value).orElseThrow());
    assertEquals("In the smog-choked dystopian Los Angeles of 2019, blade runner Rick Deckard is called out of retirement to terminate a quartet of replicants who have escaped to Earth seeking their creator for a way to extend their short life spans.", movie.details().overview().map(Overview::value).orElseThrow());
    LOGGER.info(movie);
  }
  
  @Test
  void testCreateWithoutReleaseDate() {
    var details = MovieDetails.builder()
        .title(Title.of("Blade Runner"))
        .score(Score.of(BigDecimal.valueOf(7.893)))
        .originalLanguage(LanguageCode.of("en"))
        .overview(Overview.of("In the smog-choked dystopian Los Angeles of 2019, blade runner Rick Deckard is called out of retirement to terminate a quartet of replicants who have escaped to Earth seeking their creator for a way to extend their short life spans."))
        .build();
    var movie = Movie.create(MovieId.of(GENERATOR.generate()), TmdbId.of(78), details);
    assertEquals("Blade Runner", movie.details().title().value());
    assertTrue(movie.details().releaseDate().isEmpty());
    assertEquals(7.893, movie.details().score().map(s -> s.value().doubleValue()).orElseThrow(), 0.001);
    assertEquals("en", movie.details().originalLanguage().map(LanguageCode::value).orElseThrow());
    assertEquals("In the smog-choked dystopian Los Angeles of 2019, blade runner Rick Deckard is called out of retirement to terminate a quartet of replicants who have escaped to Earth seeking their creator for a way to extend their short life spans.", movie.details().overview().map(Overview::value).orElseThrow());
    LOGGER.info(movie);
  }  
}
