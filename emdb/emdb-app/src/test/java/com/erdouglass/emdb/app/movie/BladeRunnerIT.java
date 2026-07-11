package com.erdouglass.emdb.app.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
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
import com.erdouglass.emdb.media.Gender;
import com.erdouglass.emdb.media.SaveMovie;
import com.erdouglass.emdb.media.SaveMovie.CastCredit;
import com.erdouglass.emdb.media.SaveMovie.Credits;
import com.erdouglass.emdb.media.SaveResult;
import com.erdouglass.emdb.media.show.ShowStatus;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BladeRunnerIT {
  private static final Logger LOGGER = Logger.getLogger(BladeRunnerIT.class);
  
  private Long movieId;

  @Test
  @Order(1)
  void testSaveMovie() throws IOException, InterruptedException {
    var credits = new Credits(
        List.of(
            new CastCredit("52fe4214c3a36847f800259f", 3L, "Harrison Ford", Gender.MALE, "/pjBMJVPpcZK23Vt1nzr1zEBTWrP.jpg", "Deckard", 0)),
        List.of());
    
    var command = SaveMovie.builder()
        .externalId(78)
        .title("Blade Runner")
        .releaseDate(LocalDate.parse("1982-06-25"))
        .score(BigDecimal.valueOf(7.938))
        .status(ShowStatus.RELEASED)
        .runtime(118)
        .budget(63000000L)
        .revenue(41722424L)
        .backdrop(TestHelper.image("019f1b50-9f18-77c5-86c6-118ae4ad6492.jpg"))
        .poster(TestHelper.image("019f1b50-9fc3-71f8-9302-1f79e7cf76f0.jpg"))        
        .originalLanguage("en")
        .tagline("Man has made his match... now it's his problem.")
        .overview("In the smog-choked dystopian Los Angeles of 2019, blade runner Rick Deckard is called out of retirement to terminate a quartet of replicants who have escaped to Earth seeking their creator for a way to extend their short life spans.")
        .credits(credits)
        .build();
    var request = HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(TestHelper.OBJECT_MAPPER.writeValueAsString(command)))
        .uri(UriBuilder.fromUri(TestHelper.MEDIA_URL).path("movies").build())
        .build();    
    var start = Instant.now();
    var response = TestHelper.HTTP_CLIENT.send(request, BodyHandlers.ofString());
    var et = Duration.between(start, Instant.now()).toMillis();
    assertEquals(200, response.statusCode(), "Server failed with response: " + response.body());    
    var result = TestHelper.OBJECT_MAPPER.readValue(response.body(), SaveResult.class);
    movieId = result.id();
    LOGGER.infof("Saved Blade Runner in %d ms", et);
  }
  
  @Test
  @Order(2)
  void testFindMovie() throws IOException, InterruptedException {
    var query = """
        query {
          movie(id: %d) { 
            id title releaseDate score status runtime budget revenue
            backdrop poster homepage originalLanguage tagline overview
            credits {
              cast { name profile character order }
            }
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
    assertEquals(movieId, movie.path("id").asLong());
    assertEquals("Blade Runner", movie.path("title").asText());
    assertEquals("1982-06-25", movie.path("releaseDate").asText());
    assertEquals(7.938, movie.path("score").asDouble(), 0.001);
    assertEquals("RELEASED", movie.path("status").asText());
    assertEquals(118, movie.path("runtime").asInt());
    assertEquals(63000000L, movie.path("budget").asLong());
    assertEquals(41722424L, movie.path("revenue").asLong());
    assertEquals("en", movie.path("originalLanguage").asText());
    assertEquals("Man has made his match... now it's his problem.", movie.path("tagline").asText());
    assertEquals("In the smog-choked dystopian Los Angeles of 2019, blade runner Rick Deckard is called out of retirement to terminate a quartet of replicants who have escaped to Earth seeking their creator for a way to extend their short life spans.", movie.path("overview").asText());
    
    var cast = movie.path("credits").path("cast");
    assertEquals(1, cast.size());
    assertEquals("Harrison Ford", cast.path(0).path("name").asText());
    assertEquals("Deckard", cast.path(0).path("character").asText());
    assertEquals(0, cast.path(0).path("order").asInt());
    LOGGER.infof("Found Blade Runner in %d ms", et);    
  }
}
