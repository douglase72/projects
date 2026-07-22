package com.erdouglass.emdb.app.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;

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
import com.erdouglass.emdb.media.adapter.inbound.rest.SaveMovieRequest;
import com.erdouglass.emdb.media.adapter.inbound.rest.UpdateMovieRequest;
import com.erdouglass.emdb.media.application.port.inbound.UpdateResult;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BladeRunnerIT {
  private static final Logger LOGGER = Logger.getLogger(BladeRunnerIT.class);
  
  private String movieId;
  private Long version;
  
  @Test
  @Order(1)
  void testSaveMovie() throws IOException, InterruptedException {  
    var saveRequest = SaveMovieRequest.builder()
        .sourceId("tmdb", "78")
        .title("Blade Runner")
        .releaseDate(LocalDate.parse("1982-06-25"))
        .originalLanguage("en")
        .build();
    var request = HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(TestHelper.OBJECT_MAPPER.writeValueAsString(saveRequest)))
        .uri(UriBuilder.fromUri(TestHelper.MOVIES_URL).build())
        .build();    
    var start = Instant.now();
    var response = TestHelper.HTTP_CLIENT.send(request, BodyHandlers.ofString());
    var et = Duration.between(start, Instant.now()).toMillis();
    assertEquals(201, response.statusCode(), "Server failed with response: " + response.body()); 
    var result = TestHelper.OBJECT_MAPPER.readValue(response.body(), SaveResult.class);
    movieId = result.id();
    version = result.version();
    LOGGER.infof("Saved %s (Blade Runner) in %d ms", movieId, et);
  }
  
  @Test
  @Order(2)
  void testSaveReleaseDate() throws IOException, InterruptedException {  
    var saveRequest = SaveMovieRequest.builder()
        .sourceId("tmdb", "78")
        .title("Blade Runner")
        .releaseDate(LocalDate.parse("1982-10-04"))
        .originalLanguage("en")
        .build();
    var request = HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(TestHelper.OBJECT_MAPPER.writeValueAsString(saveRequest)))
        .uri(UriBuilder.fromUri(TestHelper.MOVIES_URL).build())
        .build();    
    var start = Instant.now();
    var response = TestHelper.HTTP_CLIENT.send(request, BodyHandlers.ofString());
    var et = Duration.between(start, Instant.now()).toMillis();
    assertEquals(200, response.statusCode(), "Server failed with response: " + response.body());  
    var result = TestHelper.OBJECT_MAPPER.readValue(response.body(), SaveResult.class);
    version = result.version();
    LOGGER.infof("Saved %s (Blade Runner) in %d ms", movieId, et);
  }
  
  @Test
  @Order(3)
  void testUpdateReleaseDate() throws IOException, InterruptedException {  
    var updateRequest = UpdateMovieRequest.builder()
        .version(version)
        .title("Blade Runner")
        .releaseDate(LocalDate.parse("1982-06-25"))
        .originalLanguage("en")
        .build();
    var request = HttpRequest.newBuilder()
        .PUT(HttpRequest.BodyPublishers.ofString(TestHelper.OBJECT_MAPPER.writeValueAsString(updateRequest)))
        .uri(UriBuilder.fromUri(TestHelper.MOVIES_URL).path(movieId).build())
        .build();    
    var start = Instant.now();
    var response = TestHelper.HTTP_CLIENT.send(request, BodyHandlers.ofString());
    var et = Duration.between(start, Instant.now()).toMillis();
    assertEquals(200, response.statusCode(), "Server failed with response: " + response.body());
    var result = TestHelper.OBJECT_MAPPER.readValue(response.body(), UpdateResult.class);
    version = result.version();
    LOGGER.infof("Updated %s (Blade Runner) in %d ms", movieId, et);
  }  
  
  @Test
  @Order(4)
  void testUpdateTitle() throws IOException, InterruptedException {  
    var updateRequest = UpdateMovieRequest.builder()
        .version(version)
        .title("Blade Runner: Directors Cut")
        .releaseDate(LocalDate.parse("1982-06-25"))
        .originalLanguage("en")
        .build();
    var request = HttpRequest.newBuilder()
        .PUT(HttpRequest.BodyPublishers.ofString(TestHelper.OBJECT_MAPPER.writeValueAsString(updateRequest)))
        .uri(UriBuilder.fromUri(TestHelper.MOVIES_URL).path(movieId).build())
        .build();    
    var start = Instant.now();
    var response = TestHelper.HTTP_CLIENT.send(request, BodyHandlers.ofString());
    var et = Duration.between(start, Instant.now()).toMillis();
    assertEquals(200, response.statusCode(), "Server failed with response: " + response.body());
    var result = TestHelper.OBJECT_MAPPER.readValue(response.body(), UpdateResult.class);
    version = result.version();
    LOGGER.infof("Updated %s (Blade Runner: Directors Cut) in %d ms", movieId, et);
  }  
}
