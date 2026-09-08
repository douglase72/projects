package com.erdouglass.emdb.media.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.erdouglass.emdb.media.api.TmdbId;
import com.erdouglass.emdb.media.kernel.LanguageCode;
import com.erdouglass.emdb.media.kernel.Overview;
import com.erdouglass.emdb.media.kernel.Score;
import com.erdouglass.emdb.media.kernel.Title;
import com.erdouglass.emdb.media.movie.domain.model.Movie;
import com.erdouglass.emdb.media.movie.domain.model.MovieDetails;
import com.erdouglass.emdb.media.movie.domain.model.ReleaseDate;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MovieTest {
  private static final Logger LOGGER = Logger.getLogger(MovieTest.class);
  
  @Test
  @Order(1)
  void testCreateMovie() {
    var details = MovieDetails.builder()
        .title(Title.of("Austin Powers in Goldmember"))
        .releaseDate(ReleaseDate.from("2002-07-26"))
        .score(Score.of(BigDecimal.valueOf(5.992)))
        .originalLanguage(LanguageCode.of("en"))
        .overview(Overview.of("The world's most shagadelic spy continues his fight against Dr. Evil. This time, the diabolical doctor and his clone, Mini-Me, team up with a new foe—'70s kingpin Goldmember. While pursuing the team of villains to stop them from world domination, Austin gets help from his dad and an old girlfriend."))        
        .build();
    var movie = Movie.create(TmdbId.of(818), details);
    assertEquals(818, movie.tmdbId().value());
    assertEquals("Austin Powers in Goldmember", movie.title().value());
    assertEquals("2002-07-26", movie.releaseDate().get().value().toDateString());
    assertEquals(5.992, movie.score().get().value().doubleValue(), 0.001);
    assertEquals("en", movie.originalLanguage().get().value());
    assertEquals("The world's most shagadelic spy continues his fight against Dr. Evil. This time, the diabolical doctor and his clone, Mini-Me, team up with a new foe—'70s kingpin Goldmember. While pursuing the team of villains to stop them from world domination, Austin gets help from his dad and an old girlfriend.", movie.overview().get().value());
    LOGGER.info(movie);
  }
  
  @Test
  @Order(2)
  void testUpdateMovie() {
    var details = MovieDetails.builder()
        .title(Title.of("Austin Powers in Goldmember"))
        .releaseDate(ReleaseDate.from("2002-07-26"))
        .score(Score.of(BigDecimal.valueOf(5.992)))
        .originalLanguage(LanguageCode.of("en"))
        .overview(Overview.of("The world's most shagadelic spy continues his fight against Dr. Evil. This time, the diabolical doctor and his clone, Mini-Me, team up with a new foe—'70s kingpin Goldmember. While pursuing the team of villains to stop them from world domination, Austin gets help from his dad and an old girlfriend."))        
        .build();
    var movie = Movie.create(TmdbId.of(818), details);
    assertEquals(818, movie.tmdbId().value());
    assertEquals("Austin Powers in Goldmember", movie.title().value());
    assertEquals("2002-07-26", movie.releaseDate().get().value().toDateString());
    assertEquals(5.992, movie.score().get().value().doubleValue(), 0.001);
    assertEquals("en", movie.originalLanguage().get().value());
    assertEquals("The world's most shagadelic spy continues his fight against Dr. Evil. This time, the diabolical doctor and his clone, Mini-Me, team up with a new foe—'70s kingpin Goldmember. While pursuing the team of villains to stop them from world domination, Austin gets help from his dad and an old girlfriend.", movie.overview().get().value());
    
    details = MovieDetails.builder()
        .title(Title.of("X"))
        .score(Score.of(BigDecimal.valueOf(1.2)))
        .originalLanguage(LanguageCode.of("en"))
        .overview(Overview.of("Test overview."))
        .build();
    movie.update(details);
    assertEquals(818, movie.tmdbId().value());
    assertEquals("X", movie.title().value());
    assertTrue(movie.releaseDate().isEmpty());
    assertEquals(1.2, movie.score().get().value().doubleValue(), 0.001);
    assertEquals("en", movie.originalLanguage().get().value());
    assertEquals("Test overview.", movie.overview().get().value());
    LOGGER.info(movie);
  }
}
