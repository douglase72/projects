package com.erdouglass.emdb.test.gateway.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import com.erdouglass.emdb.media.api.Image;
import com.erdouglass.emdb.media.api.ShowStatus;
import com.erdouglass.emdb.media.api.command.SaveMovie;
import com.erdouglass.emdb.media.api.command.UpdateMovie;
import com.erdouglass.emdb.media.api.query.MovieDetails;
import com.erdouglass.emdb.test.gateway.AbstractTest;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MovieCrudIT extends AbstractTest {
  private static final Logger LOGGER = Logger.getLogger(MovieCrudIT.class);
  private static final UUID BACKDROP = UUID.fromString("019d3220-adb3-75ba-b1b2-1619de2a2fef");
  private static final UUID POSTER = UUID.fromString("019d3220-aead-702b-a997-5700e9a2076a");  
  
  private String token;
  private Long movieId;
  
  @BeforeAll
  void setupSecurity() throws IOException, InterruptedException {
    this.token = getAccessToken();
  }
  
  @Test
  @Order(1)
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
        .backdrop(Image.of(BACKDROP, "/mPMtuVB6AEulRhlfn69y5RvgmNT.jpg"))
        .poster(Image.of(POSTER, "/zp5gxcPnxv6FsDh3l7yRZurlBRr.jpg"))
        .tagline("The grooviest movie of the summer has a secret, baby!")
        .overview("The world's most shagadelic spy continues his fight against Dr. Evil. This time, the diabolical doctor and his clone, Mini-Me, team up with a new foe—'70s kingpin Goldmember. While pursuing the team of villains to stop them from world domination, Austin gets help from his dad and an old girlfriend.")
        .build();
    var request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(MOVIES_URL).build())
        .header("Authorization", "Bearer " + token)
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command))).build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(201, response.statusCode(), "Server failed with response: " + response.body());
    
    var movie = OBJECT_MAPPER.readValue(response.body(), MovieDetails.class);
    movieId = movie.id();
    LOGGER.infof("Saved Austin Powers in Goldmember in %d ms", et);
  }
  
  @Test
  @Order(2)
  void testFindMovie() throws IOException, InterruptedException {
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(MOVIES_URL).path(movieId.toString()).queryParam("append", "credits").build())
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(200, response.statusCode());
    
    var movie = OBJECT_MAPPER.readValue(response.body(), MovieDetails.class);
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
    assertEquals("019d3220-adb3-75ba-b1b2-1619de2a2fef.jpg", movie.backdrop());
    assertEquals("019d3220-aead-702b-a997-5700e9a2076a.jpg", movie.poster());
    assertEquals("The grooviest movie of the summer has a secret, baby!", movie.tagline());
    assertEquals("The world's most shagadelic spy continues his fight against Dr. Evil. This time, the diabolical doctor and his clone, Mini-Me, team up with a new foe—'70s kingpin Goldmember. While pursuing the team of villains to stop them from world domination, Austin gets help from his dad and an old girlfriend.", movie.overview());
    LOGGER.infof("Found Austin Powers in Goldmember in: %d ms", et);    
  }
  
  @Test
  @Order(3)
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
    assertEquals(200, response.statusCode(), "Server failed with response: " + response.body());
    
    var movie = OBJECT_MAPPER.readValue(response.body(), MovieDetails.class);
    assertEquals(200, response.statusCode());
    assertEquals(818, movie.tmdbId());
    assertEquals("X", movie.title());
    assertEquals("2026-02-01", movie.releaseDate().toString());
    assertEquals(6.6f, movie.score());
    assertEquals(ShowStatus.RUMORED, movie.status());
    assertEquals(120, movie.runtime());
    assertEquals(63000000, movie.budget());
    assertEquals(296938801, movie.revenue());
    assertEquals("254b1be1-daaf-44ad-99ae-38fc0b779b73.jpg", movie.backdrop());
    assertEquals("fd152dc9-da8f-4a58-a2e8-35796c8d66eb.jpg", movie.poster());    
    assertEquals("https://www.warnerbros.com/movies/austin-powers-goldmember", movie.homepage());    
    assertEquals("en", movie.originalLanguage());
    assertEquals("The grooviest movie of the summer has a secret, baby!", movie.tagline());
    assertEquals("Test overview.", movie.overview());    
    LOGGER.infof("Updated movie %d in %d ms", movieId, et);  
  }
  
  @Test
  @Order(4)
  void testDeleteMovie() throws IOException, InterruptedException {
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(MOVIES_URL).path(movieId.toString()).build())
        .header("Authorization", "Bearer " + token)
        .DELETE()
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(204, response.statusCode());
    LOGGER.infof("Deleted movie %d in: %d ms", movieId, et);    
  }
}
