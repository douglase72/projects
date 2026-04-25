package com.erdouglass.emdb.test.gateway.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDate;
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

import com.erdouglass.emdb.common.ShowStatus;
import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.common.query.MovieView;
import com.erdouglass.emdb.gateway.query.Slice;
import com.erdouglass.emdb.test.gateway.AbstractTest;
import com.fasterxml.jackson.core.type.TypeReference;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MovieFindIT extends AbstractTest {
  private static final Logger LOGGER = Logger.getLogger(MovieFindIT.class);
  
  private String token;
  
  @BeforeAll
  void setupSecurity() throws IOException, InterruptedException {
    this.token = getAccessToken();
  }
  
  @Test
  @Order(1)
  void testSaveAustinPowersInternationalManOfMystery() throws IOException, InterruptedException {
    var command = SaveMovie.builder()
        .tmdbId(816)
        .title("Austin Powers: International Man of Mystery")
        .releaseDate(LocalDate.parse("1997-05-02"))
        .score(6.6f)
        .status(ShowStatus.RELEASED)
        .build();
    var request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(MOVIES_URL).build())
        .header("Authorization", "Bearer " + token)
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command))).build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(201, response.statusCode(), "Server failed with response: " + response.body());
    LOGGER.infof("Saved Austin Powers: International Man of Mystery in %d ms", et);
  }
  
  @Test
  @Order(2)
  void testSaveAustinPowersTheSpyWhoShaggedMe() throws IOException, InterruptedException {
    var command = SaveMovie.builder()
        .tmdbId(817)
        .title("Austin Powers: The Spy Who Shagged Me")
        .releaseDate(LocalDate.parse("1999-06-08"))
        .score(6.4f)
        .status(ShowStatus.RELEASED)
        .build();
    var request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(MOVIES_URL).build())
        .header("Authorization", "Bearer " + token)
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command))).build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(201, response.statusCode(), "Server failed with response: " + response.body());
    LOGGER.infof("Saved Austin Powers: The Spy Who Shagged Me in %d ms", et);
  }
  
  @Test
  @Order(3)
  void testSaveAustinPowersInGoldmember() throws IOException, InterruptedException {
    var command = SaveMovie.builder()
        .tmdbId(818)
        .title("Austin Powers in Goldmember")
        .releaseDate(LocalDate.parse("2002-07-26"))
        .score(5.992f)
        .status(ShowStatus.RELEASED)
        .build();
    var request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(MOVIES_URL).build())
        .header("Authorization", "Bearer " + token)
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command))).build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(201, response.statusCode(), "Server failed with response: " + response.body());
    LOGGER.infof("Saved Austin Powers in Goldmember in %d ms", et);
  }
  
  @Test
  @Order(4)
  void testFindAllMovies() throws IOException, InterruptedException {
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(MOVIES_URL).queryParam("page", 1).queryParam("size", "5").build())
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(200, response.statusCode());
    
    var page = OBJECT_MAPPER.readValue(response.body(), new TypeReference<Slice<MovieView>>() {});
    assertEquals(1, page.page());
    assertEquals(5, page.size());
    assertFalse(page.hasNext());
    
    var movies = page.results();
    assertEquals("Austin Powers in Goldmember", movies.get(0).title());
    assertEquals("Austin Powers: The Spy Who Shagged Me", movies.get(1).title());
    assertEquals("Austin Powers: International Man of Mystery", movies.get(2).title());
    LOGGER.infof("Found all movies in: %d ms", et); 
  }
}
