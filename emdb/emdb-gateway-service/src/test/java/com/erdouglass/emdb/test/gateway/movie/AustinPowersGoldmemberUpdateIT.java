package com.erdouglass.emdb.test.gateway.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.erdouglass.emdb.common.ShowStatus;
import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.common.comand.UpdateMovie;
import com.erdouglass.emdb.common.query.MovieDto;
import com.erdouglass.emdb.test.gateway.AbstractTest;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AustinPowersGoldmemberUpdateIT extends AbstractTest {
  private static final Logger LOGGER = Logger.getLogger(AustinPowersGoldmemberSaveIT.class);
  
  private Long movieId;
  private String token;
  
  @BeforeAll
  void setupSecurity() throws IOException, InterruptedException {
    this.token = getAccessToken();
  }
  
  @Test
  @Order(1)
  void testSaveMovieCommand() throws IOException, InterruptedException {
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
        .backdrop(UUID.fromString("03592ec0-7ce4-4343-8fcf-8965887be2e6"))
        .poster(UUID.fromString("f4435126-75ca-4e44-9ceb-b414662b7164"))
        .tagline("The grooviest movie of the summer has a secret, baby!")
        .overview("The world's most shagadelic spy continues his fight against Dr. Evil. This time, the diabolical doctor and his clone, Mini-Me, team up with a new foe—'70s kingpin Goldmember. While pursuing the team of villains to stop them from world domination, Austin gets help from his dad and an old girlfriend.")
        .build(); 
    var request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(MOVIES_URL).build())
        .header("Authorization", "Bearer " + token)
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command))).build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    var movie = OBJECT_MAPPER.readValue(response.body(), MovieDto.class);
    movieId = movie.id();
    assertEquals(201, response.statusCode());
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
    assertEquals("03592ec0-7ce4-4343-8fcf-8965887be2e6.jpg", movie.backdrop());
    assertEquals("f4435126-75ca-4e44-9ceb-b414662b7164.jpg", movie.poster());
    assertEquals("The grooviest movie of the summer has a secret, baby!", movie.tagline());
    assertEquals("The world's most shagadelic spy continues his fight against Dr. Evil. This time, the diabolical doctor and his clone, Mini-Me, team up with a new foe—'70s kingpin Goldmember. While pursuing the team of villains to stop them from world domination, Austin gets help from his dad and an old girlfriend.", movie.overview());    
    LOGGER.infof("Saved movie %d in %d ms", movie.id(), et);    
  } 
  
  @Test
  @Order(2)
  void testUpdateMovieCommand() throws IOException, InterruptedException {
    var command = UpdateMovie.builder()
        .title("X")
        .releaseDate(LocalDate.parse("2026-02-01"))
        .score(6.6f)
        .status(ShowStatus.RUMORED)
        .runtime(120)
        .originalLanguage("en")
        .backdrop(UUID.fromString("254b1be1-daaf-44ad-99ae-38fc0b779b73"))
        .poster(UUID.fromString("fd152dc9-da8f-4a58-a2e8-35796c8d66eb"))
        .overview("Test overview.")
        .build();
    var request  = HttpRequest.newBuilder().uri(UriBuilder.fromUri(MOVIES_URL).path(movieId.toString()).build())
        .header("Authorization", "Bearer " + token)
        .PUT(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command))).build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    var movie = OBJECT_MAPPER.readValue(response.body(), MovieDto.class);
    assertEquals(200, response.statusCode());
    assertEquals(818, movie.tmdbId());
    assertEquals("X", movie.title());
    assertEquals("2026-02-01", movie.releaseDate().toString());
    assertEquals(6.6f, movie.score());
    assertEquals(ShowStatus.RUMORED, movie.status());
    assertEquals(120, movie.runtime());
    assertNull(movie.budget());
    assertNull(movie.revenue());
    assertNull(movie.homepage());
    assertEquals("en", movie.originalLanguage());
    assertEquals("254b1be1-daaf-44ad-99ae-38fc0b779b73.jpg", movie.backdrop());
    assertEquals("fd152dc9-da8f-4a58-a2e8-35796c8d66eb.jpg", movie.poster());
    assertNull(movie.tagline());
    assertEquals("Test overview.", movie.overview());    
    LOGGER.infof("Updated movie %d in %d ms", movieId, et);    
  }

}
