package com.erdouglass.emdb.app.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.Instant;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import com.erdouglass.emdb.app.TestHelper;
import com.erdouglass.emdb.media.Result;
import com.erdouglass.emdb.media.movie.adapter.in.rest.LockMovieRequest;
import com.erdouglass.emdb.media.movie.adapter.in.rest.SaveMovieRequest;
import com.erdouglass.emdb.media.movie.adapter.in.rest.UpdateMovieRequest;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BladeRunnerLockIT {
  private static final Logger LOGGER = Logger.getLogger(BladeRunnerLockIT.class);
  
  private String movieId;
  private Long version;
  
  @Test
  @Order(1)
  void testSaveMovie() throws IOException, InterruptedException {  
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
    assertEquals(201, response.statusCode(), "Server failed with response: " + response.body()); 
    var result = TestHelper.OBJECT_MAPPER.readValue(response.body(), Result.class);
    movieId = result.id();
    version = result.version();
    LOGGER.infof("Saved %s movie in %d ms", movieId, et);
  }
  
  @Test
  @Order(2)
  void testLockMovie() throws IOException, InterruptedException {  
    var lockRequest = new LockMovieRequest(true, version);
    var request = HttpRequest.newBuilder()
        .PUT(HttpRequest.BodyPublishers.ofString(TestHelper.OBJECT_MAPPER.writeValueAsString(lockRequest)))
        .uri(UriBuilder.fromUri(TestHelper.MOVIES_URL).path("lock").path(movieId).build())
        .build();    
    var start = Instant.now();
    var response = TestHelper.HTTP_CLIENT.send(request, BodyHandlers.ofString());
    var et = Duration.between(start, Instant.now()).toMillis();
    assertEquals(200, response.statusCode(), "Server failed with response: " + response.body());
    var result = TestHelper.OBJECT_MAPPER.readValue(response.body(), Result.class);
    version = result.version();
    LOGGER.infof("Locked %s movie in %d ms", movieId, et);
  }
  
  @Test
  @Order(3)
  void testUpdateLockedMovie() throws IOException, InterruptedException {  
    var updateRequest = UpdateMovieRequest.builder()
        .version(version)
        .title("Blade Runner: Directors Cut")
        .score(BigDecimal.valueOf(7.001))
        .originalLanguage("fr")
        .overview("Test overview.")
        .build();
    var request = HttpRequest.newBuilder()
        .PUT(HttpRequest.BodyPublishers.ofString(TestHelper.OBJECT_MAPPER.writeValueAsString(updateRequest)))
        .uri(UriBuilder.fromUri(TestHelper.MOVIES_URL).path(movieId).build())
        .build();    
    var response = TestHelper.HTTP_CLIENT.send(request, BodyHandlers.ofString());
    assertEquals(423, response.statusCode(), "Server failed with response: " + response.body());
  }
}
