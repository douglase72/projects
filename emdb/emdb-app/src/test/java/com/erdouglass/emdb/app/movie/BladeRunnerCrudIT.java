package com.erdouglass.emdb.app.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import com.erdouglass.emdb.app.TestHelper;
import com.erdouglass.emdb.media.movie.adapter.in.rest.SaveMovieRequest;
import com.erdouglass.emdb.media.movie.adapter.in.rest.SaveMovieResponse;
import com.erdouglass.emdb.media.movie.adapter.in.rest.UpdateMovieRequest;
import com.erdouglass.emdb.media.movie.adapter.in.rest.UpdateMovieResponse;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BladeRunnerCrudIT {
  private static final Logger LOGGER = Logger.getLogger(BladeRunnerCrudIT.class);
  
  private UUID movieId;
  
  @Test
  @Order(1)
  void testSaveMovie() throws IOException, InterruptedException {
    var saveRequest = SaveMovieRequest.builder()
        .title("Blade Runner")
        .build();
    var request = HttpRequest.newBuilder()
        .PUT(HttpRequest.BodyPublishers.ofString(TestHelper.OBJECT_MAPPER.writeValueAsString(saveRequest)))
        .uri(UriBuilder.fromUri(TestHelper.MOVIES_URL).path("tmdb/78").build())
        .build();    
    var start = Instant.now();
    var response = TestHelper.HTTP_CLIENT.send(request, BodyHandlers.ofString());
    var et = Duration.between(start, Instant.now()).toMillis();
    var result = TestHelper.OBJECT_MAPPER.readValue(response.body(), SaveMovieResponse.class);
    movieId = result.id();
    assertEquals(201, response.statusCode(), "Server failed with response: " + response.body());
    assertEquals(movieId, result.id());
    assertEquals("CREATED", result.status());
    LOGGER.infof("Saved movie: %s in %d ms", movieId, et);
  }
  
  @Test
  @Order(2)
  void testIdempotency() throws IOException, InterruptedException {
    var saveRequest = SaveMovieRequest.builder()
        .title("Blade Runner")
        .releaseDate("1982-06-25")
        .score(BigDecimal.valueOf(7.893))
        .originalLanguage("en")
        .overview("In the smog-choked dystopian Los Angeles of 2019, blade runner Rick Deckard is called out of retirement to terminate a quartet of replicants who have escaped to Earth seeking their creator for a way to extend their short life spans.")
        .build();
    var request = HttpRequest.newBuilder()
        .PUT(HttpRequest.BodyPublishers.ofString(TestHelper.OBJECT_MAPPER.writeValueAsString(saveRequest)))
        .uri(UriBuilder.fromUri(TestHelper.MOVIES_URL).path("tmdb/78").build())
        .build();    
    var start = Instant.now();
    var response = TestHelper.HTTP_CLIENT.send(request, BodyHandlers.ofString());
    var et = Duration.between(start, Instant.now()).toMillis();
    var result = TestHelper.OBJECT_MAPPER.readValue(response.body(), SaveMovieResponse.class);
    movieId = result.id();
    assertEquals(200, response.statusCode(), "Server failed with response: " + response.body());
    assertEquals(movieId, result.id());
    assertEquals("UPDATED", result.status());
    LOGGER.infof("Saved movie: %s in %d ms", movieId, et);
  }
  
  @Test
  @Order(3)
  void testFindMovie() throws IOException, InterruptedException {
    var query = """
        query {
          movie(id: "%s") { 
            id version title releaseDate score originalLanguage overview
          }
        }
        """.formatted(movieId);
    var payload = Map.of("query", query);
    var request = HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(TestHelper.OBJECT_MAPPER.writeValueAsString(payload)))
        .header("Content-Type", "application/json")
        .uri(UriBuilder.fromUri(TestHelper.GRAPHQL_URL).build())
        .build(); 
    var start = Instant.now();
    var response = TestHelper.HTTP_CLIENT.send(request, BodyHandlers.ofString());
    var et = Duration.between(start, Instant.now()).toMillis();
    var root = TestHelper.OBJECT_MAPPER.readTree(response.body());
    
    var movie = root.path("data").path("movie");
    assertTrue(root.path("errors").isMissingNode(), "GraphQL errors: " + root.path("errors"));
    assertEquals(movieId.toString(), movie.path("id").asText());
    assertEquals(1, movie.path("version").asLong());
    assertEquals("Blade Runner", movie.path("title").asText());
    assertEquals("1982-06-25", movie.path("releaseDate").asText());
    assertEquals(7.893, movie.path("score").asDouble(), 0.001);
    assertEquals("en", movie.path("originalLanguage").asText());
    assertEquals("In the smog-choked dystopian Los Angeles of 2019, blade runner Rick Deckard is called out of retirement to terminate a quartet of replicants who have escaped to Earth seeking their creator for a way to extend their short life spans.", movie.path("overview").asText());
    LOGGER.infof("Found movie: %s in %d ms", movieId, et);    
  }
  
  @Test
  @Order(4)
  void testUpdateMovie() throws IOException, InterruptedException {
    var updateRequest = UpdateMovieRequest.builder()
        .version(1L)
        .title("X")
        .releaseDate("1982-10-04")
        .score(BigDecimal.valueOf(1.2))
        .originalLanguage("en")
        .overview("Test overview")
        .build();
    var request = HttpRequest.newBuilder()
        .PUT(HttpRequest.BodyPublishers.ofString(TestHelper.OBJECT_MAPPER.writeValueAsString(updateRequest)))
        .uri(UriBuilder.fromUri(TestHelper.MOVIES_URL).path(movieId.toString()).build())
        .build();    
    var start = Instant.now();
    var response = TestHelper.HTTP_CLIENT.send(request, BodyHandlers.ofString());
    var et = Duration.between(start, Instant.now()).toMillis();
    var result = TestHelper.OBJECT_MAPPER.readValue(response.body(), UpdateMovieResponse.class);
    movieId = result.id();
    assertEquals(200, response.statusCode(), "Server failed with response: " + response.body());
    assertEquals(movieId, result.id());
    assertEquals("UPDATED", result.status());
    assertEquals(2, result.version());
    LOGGER.infof("Updated movie: %s in %d ms", movieId, et);
  }
}
