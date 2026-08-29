package com.erdouglass.emdb.media.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import com.erdouglass.emdb.media.kernel.LanguageCode;
import com.erdouglass.emdb.media.kernel.Overview;
import com.erdouglass.emdb.media.kernel.Score;
import com.erdouglass.emdb.media.kernel.Title;
import com.erdouglass.emdb.media.kernel.TmdbCreditId;
import com.erdouglass.emdb.media.kernel.TmdbId;
import com.erdouglass.emdb.media.movie.domain.model.CastDetails;
import com.erdouglass.emdb.media.movie.domain.model.CastOrder;
import com.erdouglass.emdb.media.movie.domain.model.CreditDetails;
import com.erdouglass.emdb.media.movie.domain.model.CrewDetails;
import com.erdouglass.emdb.media.movie.domain.model.Department;
import com.erdouglass.emdb.media.movie.domain.model.Movie;
import com.erdouglass.emdb.media.movie.domain.model.MovieDetails;
import com.erdouglass.emdb.media.movie.domain.model.ReleaseDate;
import com.erdouglass.emdb.media.movie.domain.model.Role;
import com.erdouglass.emdb.media.person.domain.model.Name;
import com.erdouglass.emdb.media.person.domain.model.PersonPublicId;

class MovieTest {
  private static final Logger LOGGER = Logger.getLogger(MovieTest.class);
  
  @Test
  void testCreateMovie() {
    var details = MovieDetails.builder()
        .title(Title.of("Austin Powers in Goldmember"))
        .releaseDate(ReleaseDate.from("2002-07-26"))
        .score(Score.of(BigDecimal.valueOf(5.992)))
        .originalLanguage(LanguageCode.of("en"))
        .overview(Overview.of("The world's most shagadelic spy continues his fight against Dr. Evil. This time, the diabolical doctor and his clone, Mini-Me, team up with a new foe—'70s kingpin Goldmember. While pursuing the team of villains to stop them from world domination, Austin gets help from his dad and an old girlfriend."))
        .build();
    var credits = new ArrayList<CreditDetails>();
    credits.add(CastDetails.builder()
        .tmdbId(TmdbCreditId.of("52fe427bc3a36847f8022183"))
        .personId(PersonPublicId.from(1L))
        .name(Name.of("Mike Myers"))
        .character(Role.of("Austin Powers / Dr. Evil / Goldmember / Fat Bastard"))
        .order(CastOrder.of(0))
        .build());
    credits.add(CastDetails.builder()
        .tmdbId(TmdbCreditId.of("52fe427bc3a36847f8022187"))
        .personId(PersonPublicId.from(2L))
        .name(Name.of("Beyoncé"))
        .character(Role.of("Foxxy Cleopatra"))
        .order(CastOrder.of(1))
        .build());
    credits.add(CrewDetails.builder()
        .tmdbId(TmdbCreditId.of("52fe427bc3a36847f8022189"))
        .personId(PersonPublicId.from(1L))
        .name(Name.of("Mike Myers"))
        .job(Role.of("Screenplay"))
        .department(Department.of("Writing"))
        .build());    
    var movie = Movie.create(TmdbId.of(818), details, credits);
    assertEquals(818, movie.tmdbId().value());
    assertEquals("Austin Powers in Goldmember", movie.title().value());
    assertEquals("2002-07-26", movie.releaseDate().get().value().toDateString());
    assertEquals(5.992, movie.score().get().value().doubleValue(), 0.001);
    assertEquals("en", movie.originalLanguage().get().value());
    assertEquals("The world's most shagadelic spy continues his fight against Dr. Evil. This time, the diabolical doctor and his clone, Mini-Me, team up with a new foe—'70s kingpin Goldmember. While pursuing the team of villains to stop them from world domination, Austin gets help from his dad and an old girlfriend.", movie.overview().get().value());
    assertEquals(3, movie.credits().size());
    assertEquals("Mike Myers", movie.credits().get(0).name().value());
    assertEquals("Austin Powers / Dr. Evil / Goldmember / Fat Bastard", ((CastDetails) movie.credits().get(0).details()).character().value());
    assertEquals("Beyoncé", movie.credits().get(1).name().value());
    assertEquals("Foxxy Cleopatra", ((CastDetails) movie.credits().get(1).details()).character().value());   
    assertEquals("Mike Myers", movie.credits().get(2).name().value());
    assertEquals("Screenplay", ((CrewDetails) movie.credits().get(2).details()).job().value());        
    LOGGER.info(movie);
  }
  
  @Test
  void testUpdateMovie() {
    var details = MovieDetails.builder()
        .title(Title.of("Austin Powers in Goldmember"))
        .releaseDate(ReleaseDate.from("2002-07-26"))
        .score(Score.of(BigDecimal.valueOf(5.992)))
        .originalLanguage(LanguageCode.of("en"))
        .overview(Overview.of("The world's most shagadelic spy continues his fight against Dr. Evil. This time, the diabolical doctor and his clone, Mini-Me, team up with a new foe—'70s kingpin Goldmember. While pursuing the team of villains to stop them from world domination, Austin gets help from his dad and an old girlfriend."))
        .build();
    var credits = new ArrayList<CreditDetails>();
    credits.add(CastDetails.builder()
        .tmdbId(TmdbCreditId.of("52fe427bc3a36847f8022183"))
        .personId(PersonPublicId.from(1L))
        .name(Name.of("Mike Myers"))
        .character(Role.of("Austin Powers / Dr. Evil / Goldmember / Fat Bastard"))
        .order(CastOrder.of(0))
        .build());
    credits.add(CastDetails.builder()
        .tmdbId(TmdbCreditId.of("52fe427bc3a36847f8022187"))
        .personId(PersonPublicId.from(2L))
        .name(Name.of("Beyoncé"))
        .character(Role.of("Foxxy Cleopatra"))
        .order(CastOrder.of(1))
        .build());
    credits.add(CrewDetails.builder()
        .tmdbId(TmdbCreditId.of("52fe427bc3a36847f8022189"))
        .personId(PersonPublicId.from(1L))
        .name(Name.of("Mike Myers"))
        .job(Role.of("Screenplay"))
        .department(Department.of("Writing"))
        .build());    
    var movie = Movie.create(TmdbId.of(818), details, credits);
    
    details = MovieDetails.builder()
        .title(Title.of("X"))
        .score(Score.of(BigDecimal.valueOf(1.2)))
        .originalLanguage(LanguageCode.of("en"))
        .overview(Overview.of("Test title."))
        .build();
    credits.clear();
    credits.add(CastDetails.builder()
        .tmdbId(TmdbCreditId.of("52fe427bc3a36847f8022183"))
        .personId(PersonPublicId.from(1L))
        .name(Name.of("Mike Myers"))
        .character(Role.of("Austin Powers / Dr. Evil / Goldmember / Fat Bastard"))
        .order(CastOrder.of(0))
        .build());
    credits.add(CrewDetails.builder()
        .tmdbId(TmdbCreditId.of("52fe427bc3a36847f8022189"))
        .personId(PersonPublicId.from(1L))
        .name(Name.of("Mike Myers"))
        .job(Role.of("Characters"))
        .department(Department.of("Writing"))
        .build());
    movie.update(details, credits);
    assertEquals("X", movie.title().value());
    assertTrue(movie.releaseDate().isEmpty());
    assertEquals(1.2, movie.score().get().value().doubleValue(), 0.001);
    assertEquals("en", movie.originalLanguage().get().value());
    assertEquals("Test title.", movie.overview().get().value());
    assertEquals(2, movie.credits().size());
    assertEquals("Mike Myers", movie.credits().get(0).name().value());
    assertEquals("Austin Powers / Dr. Evil / Goldmember / Fat Bastard", ((CastDetails) movie.credits().get(0).details()).character().value());
    assertEquals("Mike Myers", movie.credits().get(1).name().value());
    assertEquals("Characters", ((CrewDetails) movie.credits().get(1).details()).job().value());        
    LOGGER.info(movie);
  }
}
