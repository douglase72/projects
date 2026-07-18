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
import com.erdouglass.emdb.media.SaveMovieCommand;
import com.erdouglass.emdb.media.SaveResult;
import com.erdouglass.emdb.media.SourceId;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BladeRunnerIT {
  private static final Logger LOGGER = Logger.getLogger(BladeRunnerIT.class);
  
  private String movieId;
  
  @Test
  @Order(1)
  void testSaveMovie() throws IOException, InterruptedException {  
    var command = SaveMovieCommand.builder()
        .sourceId(new SourceId("tmdb", "78"))
        .title("Blade Runner")
        .releaseDate(LocalDate.parse("1982-06-25"))
        .build();
    var request = HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(TestHelper.OBJECT_MAPPER.writeValueAsString(command)))
        .uri(UriBuilder.fromUri(TestHelper.MOVIES_URL).build())
        .build();    
    var start = Instant.now();
    var response = TestHelper.HTTP_CLIENT.send(request, BodyHandlers.ofString());
    var et = Duration.between(start, Instant.now()).toMillis();
    assertEquals(201, response.statusCode(), "Server failed with response: " + response.body()); 
    var result = TestHelper.OBJECT_MAPPER.readValue(response.body(), SaveResult.class);
    movieId = result.id();
    LOGGER.infof("Saved %s: Blade Runner in %d ms", movieId, et);
  }
  
  @Test
  @Order(2)
  void testUpdateReleaseDate() throws IOException, InterruptedException {  
    var command = SaveMovieCommand.builder()
        .sourceId(new SourceId("tmdb", "78"))
        .title("Blade Runner")
        .releaseDate(LocalDate.parse("1982-10-04"))
        .build();
    var request = HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(TestHelper.OBJECT_MAPPER.writeValueAsString(command)))
        .uri(UriBuilder.fromUri(TestHelper.MOVIES_URL).build())
        .build();    
    var start = Instant.now();
    var response = TestHelper.HTTP_CLIENT.send(request, BodyHandlers.ofString());
    var et = Duration.between(start, Instant.now()).toMillis();
    assertEquals(200, response.statusCode(), "Server failed with response: " + response.body());    
    LOGGER.infof("Updated release date in %d ms", et);
  }  
}
