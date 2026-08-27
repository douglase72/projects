package com.erdouglass.emdb.media.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.erdouglass.emdb.media.kernel.SourceId;
import com.erdouglass.emdb.media.kernel.SourceId.Source;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieCommandRecord;
import com.erdouglass.emdb.media.movie.domain.CastCredit;
import com.erdouglass.emdb.media.movie.domain.CrewCredit;
import com.erdouglass.emdb.media.movie.domain.Movie;
import com.erdouglass.emdb.media.movie.domain.command.SaveMovieCommand.CastMember;
import com.erdouglass.emdb.media.movie.domain.command.SaveMovieCommand.CrewMember;

class MovieTest {
  private static final Logger LOGGER = Logger.getLogger(MovieTest.class);
  
  @Disabled
  @Test
  void testCreateMovie() {
    var cast = List.of(
        new CastMember("tmdb", "52fe427bc3a36847f8022183", "12073", "Mike Myers", "Austin Powers / Dr. Evil / Goldmember / Fat Bastard", 0),
        new CastMember("tmdb", "52fe427bc3a36847f8022187", "14386", "Beyoncé", "Foxxy Cleopatra", 1));
    var crew = List.of(
        new CrewMember("tmdb", "52fe427bc3a36847f8022189", "12073", "Mike Myers", "Screenplay", "Writing")); 
    var command = SaveMovieCommandRecord.builder()
      .sourceId(SourceId.of(Source.TMDB, "818"))  
      .title("Austin Powers in Goldmember")
      .releaseDate("2002-07-26")
      .score(BigDecimal.valueOf(5.992))
      .originalLanguage("en")
      .overview("The world's most shagadelic spy continues his fight against Dr. Evil. This time, the diabolical doctor and his clone, Mini-Me, team up with a new foe—'70s kingpin Goldmember. While pursuing the team of villains to stop them from world domination, Austin gets help from his dad and an old girlfriend.")
      .cast(cast)
      .crew(crew)
      .build();
    var movie = Movie.create(command);
    assertEquals("Austin Powers in Goldmember", movie.details().title().value());
    assertEquals("2002-07-26", movie.details().releaseDate().value().toDateString());
    assertEquals(5.992, movie.details().score().value().doubleValue(), 0.001);
    assertEquals("en", movie.details().originalLanguage().value());
    assertEquals("The world's most shagadelic spy continues his fight against Dr. Evil. This time, the diabolical doctor and his clone, Mini-Me, team up with a new foe—'70s kingpin Goldmember. While pursuing the team of villains to stop them from world domination, Austin gets help from his dad and an old girlfriend.", movie.details().overview().value());   
    assertEquals(3, movie.credits().size());
    assertEquals("Mike Myers", ((CastCredit) movie.credits().get(0)).name().value());
    assertEquals("Beyoncé", ((CastCredit) movie.credits().get(1)).name().value());
    assertEquals("Screenplay", ((CrewCredit) movie.credits().get(2)).job().value());
    LOGGER.info(movie);
  }
  
  @Test
  void testUpdateMovieCredits() {
    var cast = List.of(
        new CastMember("tmdb", "52fe427bc3a36847f8022183", "12073", "Mike Myers", "Austin Powers / Dr. Evil / Goldmember / Fat Bastard", 0),
        new CastMember("tmdb", "52fe427bc3a36847f8022187", "14386", "Beyoncé", "Foxxy Cleopatra", 1));
    var crew = List.of(
        new CrewMember("tmdb", "52fe427bc3a36847f8022189", "12073", "Mike Myers", "Screenplay", "Writing")); 
    var command = SaveMovieCommandRecord.builder()
      .sourceId(SourceId.of(Source.TMDB, "818"))
      .title("Austin Powers in Goldmember")
      .releaseDate("2002-07-26")
      .score(BigDecimal.valueOf(5.992))
      .originalLanguage("en")
      .overview("The world's most shagadelic spy continues his fight against Dr. Evil. This time, the diabolical doctor and his clone, Mini-Me, team up with a new foe—'70s kingpin Goldmember. While pursuing the team of villains to stop them from world domination, Austin gets help from his dad and an old girlfriend.")
      .cast(cast)
      .crew(crew)
      .build();
    var movie = Movie.create(command);
    assertEquals(3, movie.credits().size());
    assertEquals("Mike Myers", ((CastCredit) movie.credits().get(0)).name().value());
    assertEquals("Beyoncé", ((CastCredit) movie.credits().get(1)).name().value());
    assertEquals("Screenplay", ((CrewCredit) movie.credits().get(2)).job().value());    
    
    cast = List.of(
        new CastMember("tmdb", "52fe427bc3a36847f8022183", "12073", "Mike Myers", "Austin Powers / Dr. Evil / Goldmember / Fat Bastard", 0),
        new CastMember("tmdb", "52fe427bc3a36847f802218b", "13922", "Seth Green", "Scott Evil", 2));
    crew = List.of(
        new CrewMember("tmdb", "52fe427bc3a36847f8022189", "12073", "Mike Myers", "Characters", "Writing"));
    command = SaveMovieCommandRecord.builder()
        .sourceId(SourceId.of(Source.TMDB, "818"))
        .title("Austin Powers in Goldmember")
        .releaseDate("2002-07-26")
        .score(BigDecimal.valueOf(5.992))
        .originalLanguage("en")
        .overview("The world's most shagadelic spy continues his fight against Dr. Evil. This time, the diabolical doctor and his clone, Mini-Me, team up with a new foe—'70s kingpin Goldmember. While pursuing the team of villains to stop them from world domination, Austin gets help from his dad and an old girlfriend.")
        .cast(cast)
        .crew(crew)
        .build();    
    movie.update(command);
    assertEquals(3, movie.credits().size());
    assertEquals("Mike Myers", ((CastCredit) movie.credits().get(0)).name().value());
    assertEquals("Seth Green", ((CastCredit) movie.credits().get(1)).name().value());
    assertEquals("Characters", ((CrewCredit) movie.credits().get(2)).job().value());
    LOGGER.info(movie);
  }
}
