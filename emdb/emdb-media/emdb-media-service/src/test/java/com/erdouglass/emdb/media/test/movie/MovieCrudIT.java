package com.erdouglass.emdb.media.test.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import com.erdouglass.emdb.media.api.ShowStatus;
import com.erdouglass.emdb.media.api.command.SaveMovie;
import com.erdouglass.emdb.media.api.query.MovieResponse;
import com.erdouglass.emdb.media.test.TestHelper;

class MovieCrudIT {
  private static final Logger LOGGER = Logger.getLogger(MovieCrudIT.class);
  
  @Test
  void testSaveMovie() throws IOException, InterruptedException {
    var command = SaveMovie.builder()
        .tmdbId(818)
        .title("Austin Powers in Goldmember")
        .releaseDate(LocalDate.parse("2002-07-26"))
        .score(5.992f)
        .status(ShowStatus.RELEASED)
        .runtime(94)
        .budget(63000000)
        .revenue(296938801)
        .homepage("https://www.warnerbros.com/movies/austin-powers-goldmember")
        .originalLanguage("en")
        .tmdbBackdrop("/kuPpElzfYnzsCye0hF8EbJSrvwo.jpg")
        .tmdbPoster("/n8V61f1v7idya4WJzGEJNoIp9iL.jpg")
        .tagline("The grooviest movie of the summer has a secret, baby!")
        .overview("The world's most shagadelic spy continues his fight against Dr. Evil. This time, the diabolical doctor and his clone, Mini-Me, team up with a new foe—'70s kingpin Goldmember. While pursuing the team of villains to stop them from world domination, Austin gets help from his dad and an old girlfriend.")
        .build();
    var request = HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(TestHelper.OBJECT_MAPPER.writeValueAsString(command)))
        .uri(UriBuilder.fromUri(TestHelper.MOVIES_URL).build())
        .build();    
    var start = Instant.now();
    var response = TestHelper.HTTP_CLIENT.send(request, BodyHandlers.ofString());
    var et = Duration.between(start, Instant.now()).toMillis();
    assertEquals(201, response.statusCode(), "Server failed with response: " + response.body());
    
    var movie = TestHelper.OBJECT_MAPPER.readValue(response.body(), MovieResponse.class);
    assertEquals(818, movie.tmdbId());
    assertEquals("Austin Powers in Goldmember", movie.title());
    assertEquals("2002-07-26", movie.releaseDate().toString());
    assertEquals(5.992f, movie.score());
    assertEquals(ShowStatus.RELEASED, movie.status());
    assertEquals(94, movie.runtime());
    assertEquals(63000000, movie.budget());
    assertEquals(296938801, movie.revenue());
    assertEquals("https://www.warnerbros.com/movies/austin-powers-goldmember", movie.homepage());
    assertEquals("en", movie.originalLanguage());
    assertEquals("The grooviest movie of the summer has a secret, baby!", movie.tagline());
    assertEquals("The world's most shagadelic spy continues his fight against Dr. Evil. This time, the diabolical doctor and his clone, Mini-Me, team up with a new foe—'70s kingpin Goldmember. While pursuing the team of villains to stop them from world domination, Austin gets help from his dad and an old girlfriend.", movie.overview());
    LOGGER.infof("Saved Austin Powers in Goldmember in %d ms", et);    
  }
}
