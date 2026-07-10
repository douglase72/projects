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
import com.erdouglass.emdb.media.SaveMovie.CrewCredit;
import com.erdouglass.emdb.media.SaveResult;
import com.erdouglass.emdb.media.show.ShowStatus;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GoldmemberCrudIT {
  private static final Logger LOGGER = Logger.getLogger(GoldmemberCrudIT.class);
  
  private Long movieId;

  @Test
  @Order(1)
  void testSaveMovie() throws IOException, InterruptedException {
    var credits = new Credits(
        List.of(
            new CastCredit("52fe427bc3a36847f8022183", 12073L, "Mike Myers", Gender.MALE,  "/gjfDl52Kk02MPgUYFjs9bOy33OY.jpg", "Austin Powers / Dr. Evil / Goldmember / Fat Bastard", 0),
            new CastCredit("52fe427bc3a36847f802218b", 13922L, "Seth Green", Gender.MALE,  "/l4No5Eu6j0U80hCIkaSn17AOWrj.jpg", "Scott Evil", 2),
            new CastCredit("52fe427bc3a36847f8022187", 14386L, "Beyoncé", Gender.FEMALE,   "/2HbjNtiCtmbArEnELuDFU7knaVK.jpg", "Foxxy Cleopatra", 1)),
        List.of(
            new CrewCredit("52fe427bc3a36847f8022107", 12073L, "Mike Myers", Gender.MALE, "/gjfDl52Kk02MPgUYFjs9bOy33OY.jpg", "Producer"),
            new CrewCredit("6758f532ef269d0b88e3939a", 12073L, "Mike Myers", Gender.MALE, "/gjfDl52Kk02MPgUYFjs9bOy33OY.jpg", "Characters")));   
    
    var command = SaveMovie.builder()
        .externalId(818)
        .title("Austin Powers in Goldmember")
        .releaseDate(LocalDate.parse("2002-07-26"))
        .score(BigDecimal.valueOf(5.992))
        .status(ShowStatus.RELEASED)
        .runtime(94)
        .budget(63000000L)
        .revenue(296938801L)
        .backdrop(TestHelper.image("019e5c92-5a24-7517-8b7a-3734166ad76a.jpg"))
        .poster(TestHelper.image("019e5c8d-efdc-7687-b6c7-a6e822fb6d6d.jpg"))        
        .homepage("https://www.warnerbros.com/movies/austin-powers-goldmember")
        .originalLanguage("en")
        .tagline("The grooviest movie of the summer has a secret, baby!")
        .overview("The world's most shagadelic spy continues his fight against Dr. Evil. This time, the diabolical doctor and his clone, Mini-Me, team up with a new foe—'70s kingpin Goldmember. While pursuing the team of villains to stop them from world domination, Austin gets help from his dad and an old girlfriend.")
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
    LOGGER.infof("Saved Austin Powers in Goldmember in %d ms", et);
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
              crew { name profile job }
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
    assertEquals("Austin Powers in Goldmember", movie.path("title").asText());
    assertEquals("2002-07-26", movie.path("releaseDate").asText());
    assertEquals(5.992, movie.path("score").asDouble(), 0.001);
    assertEquals("RELEASED", movie.path("status").asText());
    assertEquals(94, movie.path("runtime").asInt());
    assertEquals(63000000L, movie.path("budget").asLong());
    assertEquals(296938801L, movie.path("revenue").asLong());
    assertEquals("https://www.warnerbros.com/movies/austin-powers-goldmember", movie.path("homepage").asText());
    assertEquals("en", movie.path("originalLanguage").asText());
    assertEquals("The grooviest movie of the summer has a secret, baby!", movie.path("tagline").asText());
    assertEquals("The world's most shagadelic spy continues his fight against Dr. Evil. This time, the diabolical doctor and his clone, Mini-Me, team up with a new foe—'70s kingpin Goldmember. While pursuing the team of villains to stop them from world domination, Austin gets help from his dad and an old girlfriend.", movie.path("overview").asText());
    
    var cast = movie.path("credits").path("cast");
    assertEquals(3, cast.size());
    assertEquals("Mike Myers", cast.path(0).path("name").asText());
    assertEquals("Austin Powers / Dr. Evil / Goldmember / Fat Bastard", cast.path(0).path("character").asText());
    assertEquals(0, cast.path(0).path("order").asInt());
    assertEquals("Beyoncé", cast.path(1).path("name").asText());
    assertEquals("Foxxy Cleopatra", cast.path(1).path("character").asText());
    assertEquals(1, cast.path(1).path("order").asInt());
    assertEquals("Seth Green", cast.path(2).path("name").asText());
    assertEquals("Scott Evil", cast.path(2).path("character").asText());
    assertEquals(2, cast.path(2).path("order").asInt());
   
    var crew = movie.path("credits").path("crew");
    assertEquals(2, crew.size());
    assertEquals("Mike Myers", crew.path(0).path("name").asText());
    assertEquals("Producer", crew.path(0).path("job").asText());
    assertEquals("Mike Myers", crew.path(1).path("name").asText());
    assertEquals("Characters", crew.path(1).path("job").asText());
    LOGGER.infof("Found Austin Powers in Goldmember in %d ms", et);    
  }  
}
