package com.erdouglass.emdb.app.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
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
import com.erdouglass.emdb.media.SavePerson;
import com.erdouglass.emdb.media.SaveResult;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HarrisonFordCrudIT {
  private static final Logger LOGGER = Logger.getLogger(HarrisonFordCrudIT.class);
  
  private Long personId;
  
  @Test
  @Order(1)
  void testSavePerson() throws IOException, InterruptedException {
    var command = SavePerson.builder()
        .externalId(3)
        .name("Harrison Ford")
        .birthDate(LocalDate.parse("1942-07-13"))
        .birthPlace("Chicago, Illinois, USA")
        .gender(Gender.MALE)
        .profile(TestHelper.image("019e6228-f9af-7427-b909-090453853b6b.jpg"))
        .biography("Legendary Hollywood Icon Harrison Ford was born on July 13, 1942 in Chicago, Illinois. His family history includes a strong lineage of actors, radio personalities, and models. Ford attended public high school in Park Ridge, Illinois where he was a member of the school Radio Station WMTH. Ford worked as the lead voice for sports reporting at WMTH for several years. Acting wasn't a major interest to Ford until his junior year at Ripon College when he first took an acting class. Ford's career started in 1964 when he travelled to California in search of a voice-over job. He never received that position, but instead signed a contract with Columbia Pictures where he earned $150 weekly to play small fill in roles in various films.\n\nThrough the '60s Ford worked on several TV shows including Gunsmoke, Ironside, Kung Fu, and American Style. It wasn't until 1967 that he received his first credited role in the Western film, A Time for Killing. Dissatisfied with the meager roles he was being offered, Ford took a hiatus from acting to work as a self-employed carpenter. This seemingly odd diversion turned out to be a blessing in disguise for Harrison's acting career when he was soon hired by famous film producer George Lucas. This was a turning point in Ford's life that led to him be casted in milestone roles such as Han Solo and Indiana Jones.\n\nSince his most famous roles in the original Star Wars trilogy and Raiders of the Lost Ark, Ford has appeared in over 40 films. Many criticize his late-career work, saying his performances have been lackluster, leading to commercially disappointing films. Ford has always worked hard to protect his off-screen private life, keeping details about his children and marriages quiet. He has a total of five children including one recent adoption with third and current wife Calista Flockhart. In addition to acting, Ford is passionate about environmental conservation, aviation, and archeology.")        
        .build();
    var request = HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(TestHelper.OBJECT_MAPPER.writeValueAsString(command)))
        .uri(UriBuilder.fromUri(TestHelper.MEDIA_URL).path("people").build())
        .build(); 
    var start = Instant.now();
    var response = TestHelper.HTTP_CLIENT.send(request, BodyHandlers.ofString());
    var et = Duration.between(start, Instant.now()).toMillis();
    assertEquals(200, response.statusCode(), "Server failed with response: " + response.body());   
    var result = TestHelper.OBJECT_MAPPER.readValue(response.body(), SaveResult.class);
    personId = result.id();
    LOGGER.infof("Saved Harrison Ford in %d ms", et);       
  }
  
  @Test
  @Order(2)
  void testFindPerson() throws IOException, InterruptedException {
    var query = """
        query {
          person(id: %d) { 
            id name birthDate deathDate gender profile birthPlace biography
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
    assertEquals(personId, person.path("id").asLong());
    assertEquals("Harrison Ford", person.path("name").asText());
    assertEquals("1942-07-13", person.path("birthDate").asText());
    assertTrue(person.path("deathDate").isNull());
    assertEquals("MALE", person.path("gender").asText());
    assertEquals("Chicago, Illinois, USA", person.path("birthPlace").asText());
    assertEquals("Legendary Hollywood Icon Harrison Ford was born on July 13, 1942 in Chicago, Illinois. His family history includes a strong lineage of actors, radio personalities, and models. Ford attended public high school in Park Ridge, Illinois where he was a member of the school Radio Station WMTH. Ford worked as the lead voice for sports reporting at WMTH for several years. Acting wasn't a major interest to Ford until his junior year at Ripon College when he first took an acting class. Ford's career started in 1964 when he travelled to California in search of a voice-over job. He never received that position, but instead signed a contract with Columbia Pictures where he earned $150 weekly to play small fill in roles in various films.\n\nThrough the '60s Ford worked on several TV shows including Gunsmoke, Ironside, Kung Fu, and American Style. It wasn't until 1967 that he received his first credited role in the Western film, A Time for Killing. Dissatisfied with the meager roles he was being offered, Ford took a hiatus from acting to work as a self-employed carpenter. This seemingly odd diversion turned out to be a blessing in disguise for Harrison's acting career when he was soon hired by famous film producer George Lucas. This was a turning point in Ford's life that led to him be casted in milestone roles such as Han Solo and Indiana Jones.\n\nSince his most famous roles in the original Star Wars trilogy and Raiders of the Lost Ark, Ford has appeared in over 40 films. Many criticize his late-career work, saying his performances have been lackluster, leading to commercially disappointing films. Ford has always worked hard to protect his off-screen private life, keeping details about his children and marriages quiet. He has a total of five children including one recent adoption with third and current wife Calista Flockhart. In addition to acting, Ford is passionate about environmental conservation, aviation, and archeology.", person.path("biography").asText());        
    LOGGER.infof("Found Harrison Ford in %d ms", et);    
  }
}
