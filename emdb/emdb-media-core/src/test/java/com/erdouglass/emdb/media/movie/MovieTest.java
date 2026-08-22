package com.erdouglass.emdb.media.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import com.erdouglass.emdb.media.TmdbId;
import com.erdouglass.emdb.media.kernel.LanguageCode;
import com.erdouglass.emdb.media.kernel.Overview;
import com.erdouglass.emdb.media.kernel.Score;
import com.erdouglass.emdb.media.kernel.Title;
import com.erdouglass.emdb.media.movie.domain.CreditId;
import com.erdouglass.emdb.media.movie.domain.Movie;
import com.erdouglass.emdb.media.movie.domain.MovieCredit;
import com.erdouglass.emdb.media.movie.domain.MovieDetails;
import com.erdouglass.emdb.media.movie.domain.MovieId;
import com.erdouglass.emdb.media.movie.domain.ReleaseDate;
import com.erdouglass.emdb.media.movie.domain.exception.LockedMovieException;

class MovieTest {
  private static final UUID ID = UUID.fromString("01a00bcf-1e3a-76b6-980c-f08e90e21a95");
  private static final Logger LOGGER = Logger.getLogger(MovieTest.class);
  
  @Test
  void testCreateCompleteMovie() {
    var credits = List.of(MovieCredit.builder()
        .id(CreditId.of(UUID.fromString("01a00bcf-1e3a-76b6-980c-f08e90e21a96")))
        .tmdbId(TmdbId.of("52fe427bc3a36847f8022183"))
        .build());
    var details = MovieDetails.builder()
        .title(Title.of("Blade Runner"))
        .releaseDate(ReleaseDate.from("1982-06-25"))
        .score(Score.of(BigDecimal.valueOf(7.893)))
        .originalLanguage(LanguageCode.of("en"))
        .overview(Overview.of("In the smog-choked dystopian Los Angeles of 2019, blade runner Rick Deckard is called out of retirement to terminate a quartet of replicants who have escaped to Earth seeking their creator for a way to extend their short life spans."))
        .credits(null)
        .build();
    var movie = Movie.create(MovieId.of(ID), TmdbId.of(78), details);
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
    var details = MovieDetails.builder()
        .title(Title.of("Blade Runner"))
        .score(Score.of(BigDecimal.valueOf(7.893)))
        .originalLanguage(LanguageCode.of("en"))
        .overview(Overview.of("In the smog-choked dystopian Los Angeles of 2019, blade runner Rick Deckard is called out of retirement to terminate a quartet of replicants who have escaped to Earth seeking their creator for a way to extend their short life spans."))
        .build();
    var movie = Movie.create(MovieId.of(ID), TmdbId.of(78), details);
    assertEquals("Blade Runner", movie.details().title().value());
    assertTrue(movie.details().releaseDate().isEmpty());
    assertEquals(7.893, movie.details().score().map(s -> s.value().doubleValue()).orElseThrow(), 0.001);
    assertEquals("en", movie.details().originalLanguage().map(LanguageCode::value).orElseThrow());
    assertEquals("In the smog-choked dystopian Los Angeles of 2019, blade runner Rick Deckard is called out of retirement to terminate a quartet of replicants who have escaped to Earth seeking their creator for a way to extend their short life spans.", movie.details().overview().map(Overview::value).orElseThrow());
    LOGGER.info(movie);
  }
  
  @Test
  void testLocked() {
    var details = MovieDetails.builder()
        .title(Title.of("Blade Runner"))
        .releaseDate(ReleaseDate.from("1982-06-25"))
        .score(Score.of(BigDecimal.valueOf(7.893)))
        .originalLanguage(LanguageCode.of("en"))
        .overview(Overview.of("In the smog-choked dystopian Los Angeles of 2019, blade runner Rick Deckard is called out of retirement to terminate a quartet of replicants who have escaped to Earth seeking their creator for a way to extend their short life spans."))
        .build();
    var movie = Movie.create(MovieId.of(ID), TmdbId.of(78), details);
    movie.lock(true);
    
    var updatedDetails = MovieDetails.builder()
        .title(Title.of("Blade Runner"))
        .releaseDate(ReleaseDate.from("1982-10-04"))
        .score(Score.of(BigDecimal.valueOf(7.893)))
        .originalLanguage(LanguageCode.of("en"))
        .overview(Overview.of("In the smog-choked dystopian Los Angeles of 2019, blade runner Rick Deckard is called out of retirement to terminate a quartet of replicants who have escaped to Earth seeking their creator for a way to extend their short life spans."))
        .build();
    var e = assertThrows(LockedMovieException.class, () -> movie.update(updatedDetails));
    assertTrue(e.getMessage().contains("Blade Runner"));
  }
}
