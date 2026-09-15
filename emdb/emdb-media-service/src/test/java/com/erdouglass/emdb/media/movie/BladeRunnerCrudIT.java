package com.erdouglass.emdb.media.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import com.erdouglass.emdb.media.TestHelper;
import com.erdouglass.emdb.media.dto.SaveResponse;
import com.erdouglass.emdb.media.movie.adapter.in.rest.SaveMovieRequest;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BladeRunnerCrudIT {
  private static final Logger LOGGER = Logger.getLogger(BladeRunnerCrudIT.class);
  
  private UUID movieId;
  
  @Test
  @Order(1)
  void testCreateMovie() throws IOException, InterruptedException {
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
    var result = TestHelper.OBJECT_MAPPER.readValue(response.body(), SaveResponse.class);
    movieId = result.id();
    assertEquals(201, response.statusCode(), "Server failed with response: " + response.body()); 
    assertEquals(movieId, result.id());
    assertEquals("CREATED", result.status());
    LOGGER.infof("Created movie in %d ms", et);
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
    var result = TestHelper.OBJECT_MAPPER.readValue(response.body(), SaveResponse.class);
    movieId = result.id();
    assertEquals(200, response.statusCode(), "Server failed with response: " + response.body());
    assertEquals(movieId, result.id());
    assertEquals("UPDATED", result.status());
    LOGGER.infof("Updated movie in %d ms", et);
  }
}
