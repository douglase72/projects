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

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import com.erdouglass.emdb.app.TestHelper;
import com.erdouglass.emdb.media.SaveResult;
import com.erdouglass.emdb.media.adapter.inbound.rest.movie.SaveMovieRequest;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BladeRunnerQueryIT {
  private static final Logger LOGGER = Logger.getLogger(BladeRunnerQueryIT.class);
  
  private String movieId;
  
  @Test
  @Order(1)
  void testSaveMovie() throws IOException, InterruptedException {  
    var saveRequest = SaveMovieRequest.builder()
        .title("Blade Runner")
        .releaseDate("1982-06-25")
        .score(BigDecimal.valueOf(7.893))
        .originalLanguage("en")
        .build();
    var request = HttpRequest.newBuilder()
        .PUT(HttpRequest.BodyPublishers.ofString(TestHelper.OBJECT_MAPPER.writeValueAsString(saveRequest)))
        .uri(UriBuilder.fromUri(TestHelper.MOVIES_URL).path("tmdb/78").build())
        .build();    
    var start = Instant.now();
    var response = TestHelper.HTTP_CLIENT.send(request, BodyHandlers.ofString());
    var et = Duration.between(start, Instant.now()).toMillis();
    assertEquals(201, response.statusCode(), "Server failed with response: " + response.body()); 
    var result = TestHelper.OBJECT_MAPPER.readValue(response.body(), SaveResult.class);
    movieId = result.id();
    LOGGER.infof("Saved %s (Blade Runner) in %d ms", movieId, et);
  }
  
  @Test
  @Order(2)
  void testFindMovie() throws IOException, InterruptedException {
    var query = """
        query {
          movie(id: "%s") { 
            id version title releaseDate score originalLanguage
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
    assertTrue(root.path("errors").isMissingNode(), "GraphQL errors: " + root.path("errors"));
    
    var movie = root.path("data").path("movie");
    assertEquals(movieId, movie.path("id").asText());
    assertEquals(0, movie.path("version").asLong());
    assertEquals("Blade Runner", movie.path("title").asText());
    assertEquals("1982-06-25", movie.path("releaseDate").asText());
    assertEquals(7.893, movie.path("score").asDouble(), 0.001);
    assertEquals("en", movie.path("originalLanguage").asText());
    LOGGER.infof("Found %s (Blade Runner) in %d ms", movieId, et);    
  }
}
