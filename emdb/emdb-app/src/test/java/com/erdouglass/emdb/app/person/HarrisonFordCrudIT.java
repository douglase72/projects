package com.erdouglass.emdb.app.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
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
import com.erdouglass.emdb.media.Result;
import com.erdouglass.emdb.media.person.adapter.in.rest.SavePersonRequest;
import com.erdouglass.emdb.media.person.adapter.in.rest.UpdatePersonRequest;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HarrisonFordCrudIT {
  private static final Logger LOGGER = Logger.getLogger(HarrisonFordCrudIT.class);
  
  private String personId;
  private Long version;
  
  @Test
  @Order(1)
  void testSavePerson() throws IOException, InterruptedException {
    var saveRequest = SavePersonRequest.builder()
        .name("Harrison Ford")
        .birthDate("1942-07-13")
        .gender("Male")
        .biography("Legendary Hollywood Icon Harrison Ford was born on July 13, 1942 in Chicago, Illinois. His family history includes a strong lineage of actors, radio personalities, and models. Ford attended public high school in Park Ridge, Illinois where he was a member of the school Radio Station WMTH. Ford worked as the lead voice for sports reporting at WMTH for several years. Acting wasn't a major interest to Ford until his junior year at Ripon College when he first took an acting class. Ford's career started in 1964 when he travelled to California in search of a voice-over job. He never received that position, but instead signed a contract with Columbia Pictures where he earned $150 weekly to play small fill in roles in various films.\n\nThrough the '60s Ford worked on several TV shows including Gunsmoke, Ironside, Kung Fu, and American Style. It wasn't until 1967 that he received his first credited role in the Western film, A Time for Killing. Dissatisfied with the meager roles he was being offered, Ford took a hiatus from acting to work as a self-employed carpenter. This seemingly odd diversion turned out to be a blessing in disguise for Harrison's acting career when he was soon hired by famous film producer George Lucas. This was a turning point in Ford's life that led to him be casted in milestone roles such as Han Solo and Indiana Jones.\n\nSince his most famous roles in the original Star Wars trilogy and Raiders of the Lost Ark, Ford has appeared in over 40 films. Many criticize his late-career work, saying his performances have been lackluster, leading to commercially disappointing films. Ford has always worked hard to protect his off-screen private life, keeping details about his children and marriages quiet. He has a total of five children including one recent adoption with third and current wife Calista Flockhart. In addition to acting, Ford is passionate about environmental conservation, aviation, and archeology.") 
        .build();
    var request = HttpRequest.newBuilder()
        .PUT(HttpRequest.BodyPublishers.ofString(TestHelper.OBJECT_MAPPER.writeValueAsString(saveRequest)))
        .uri(UriBuilder.fromUri(TestHelper.PEOPLE_URL).path("tmdb/3").build())
        .build();
    var start = Instant.now();
    var response = TestHelper.HTTP_CLIENT.send(request, BodyHandlers.ofString());
    var et = Duration.between(start, Instant.now()).toMillis();
    assertEquals(201, response.statusCode(), "Server failed with response: " + response.body()); 
    var result = TestHelper.OBJECT_MAPPER.readValue(response.body(), Result.class);
    personId = result.id();
    version = result.version();    
    LOGGER.infof("Saved person: %s in %d ms", personId, et);
  }
  
  @Test
  @Order(2)
  void testFindSavedPerson() throws IOException, InterruptedException {
    var query = """
        query {
          person(id: "%s") { 
            id version name birthDate deathDate gender biography
          }
        }
        """.formatted(personId);
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
    
    var person = root.path("data").path("person");
    assertEquals(personId, person.path("id").asText());
    assertEquals(0, person.path("version").asLong());
    assertEquals("Harrison Ford", person.path("name").asText());
    assertEquals("1942-07-13", person.path("birthDate").asText());
    assertTrue(person.path("deathDate").isNull());
    assertEquals("MALE", person.path("gender").asText());
    assertEquals("Legendary Hollywood Icon Harrison Ford was born on July 13, 1942 in Chicago, Illinois. His family history includes a strong lineage of actors, radio personalities, and models. Ford attended public high school in Park Ridge, Illinois where he was a member of the school Radio Station WMTH. Ford worked as the lead voice for sports reporting at WMTH for several years. Acting wasn't a major interest to Ford until his junior year at Ripon College when he first took an acting class. Ford's career started in 1964 when he travelled to California in search of a voice-over job. He never received that position, but instead signed a contract with Columbia Pictures where he earned $150 weekly to play small fill in roles in various films.\n\nThrough the '60s Ford worked on several TV shows including Gunsmoke, Ironside, Kung Fu, and American Style. It wasn't until 1967 that he received his first credited role in the Western film, A Time for Killing. Dissatisfied with the meager roles he was being offered, Ford took a hiatus from acting to work as a self-employed carpenter. This seemingly odd diversion turned out to be a blessing in disguise for Harrison's acting career when he was soon hired by famous film producer George Lucas. This was a turning point in Ford's life that led to him be casted in milestone roles such as Han Solo and Indiana Jones.\n\nSince his most famous roles in the original Star Wars trilogy and Raiders of the Lost Ark, Ford has appeared in over 40 films. Many criticize his late-career work, saying his performances have been lackluster, leading to commercially disappointing films. Ford has always worked hard to protect his off-screen private life, keeping details about his children and marriages quiet. He has a total of five children including one recent adoption with third and current wife Calista Flockhart. In addition to acting, Ford is passionate about environmental conservation, aviation, and archeology.", person.path("biography").asText());
    LOGGER.infof("Found saved person: %s in %d ms", personId, et);    
  }
  
  @Test
  @Order(3)
  void testUpdatePerson() throws IOException, InterruptedException {
    var updateRequest = UpdatePersonRequest.builder()
        .version(version)
        .name("Henrietta J. Ford")
        .birthDate("1962-01-01")
        .deathDate("2020-12-31")
        .gender("Female")
        .biography("Test biography") 
        .build();
    var request = HttpRequest.newBuilder()
        .PUT(HttpRequest.BodyPublishers.ofString(TestHelper.OBJECT_MAPPER.writeValueAsString(updateRequest)))
        .uri(UriBuilder.fromUri(TestHelper.PEOPLE_URL).path(personId).build())
        .build();
    var start = Instant.now();
    var response = TestHelper.HTTP_CLIENT.send(request, BodyHandlers.ofString());
    var et = Duration.between(start, Instant.now()).toMillis();
    assertEquals(200, response.statusCode(), "Server failed with response: " + response.body()); 
    var result = TestHelper.OBJECT_MAPPER.readValue(response.body(), Result.class);
    version = result.version();    
    LOGGER.infof("Updated person: %s in %d ms", personId, et);
  }
  
  @Test
  @Order(4)
  void testFindUpdatedPerson() throws IOException, InterruptedException {
    var query = """
        query {
          person(id: "%s") { 
            id version name birthDate deathDate gender biography
          }
        }
        """.formatted(personId);
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
    
    var person = root.path("data").path("person");
    assertEquals(personId, person.path("id").asText());
    assertEquals(1, person.path("version").asLong());
    assertEquals("Henrietta J. Ford", person.path("name").asText());
    assertEquals("1962-01-01", person.path("birthDate").asText());
    assertEquals("2020-12-31", person.path("deathDate").asText());
    assertEquals("FEMALE", person.path("gender").asText());
    assertEquals("Test biography", person.path("biography").asText());
    LOGGER.infof("Found updated person: %s in %d ms", personId, et);    
  }
  
  @Test
  @Order(5)
  void testDeletePerson() throws IOException, InterruptedException {
    var request = HttpRequest.newBuilder()
        .DELETE()
        .uri(UriBuilder.fromUri(TestHelper.PEOPLE_URL).path(personId).build())
        .build();
    var start = Instant.now();
    var response = TestHelper.HTTP_CLIENT.send(request, BodyHandlers.ofString());
    var et = Duration.between(start, Instant.now()).toMillis();
    assertEquals(204, response.statusCode());
    LOGGER.infof("Deleted person: %s in: %d ms", personId, et);    
  }
}
