package com.erdouglass.emdb.app.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
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
import com.erdouglass.emdb.media.movie.SaveMovie;
import com.erdouglass.emdb.media.movie.SaveMovie.CrewCredit;
import com.erdouglass.emdb.media.person.Gender;
import com.erdouglass.emdb.media.person.PersonDto;
import com.erdouglass.emdb.media.person.SavePerson;
import com.erdouglass.emdb.media.show.ShowStatus;
import com.fasterxml.jackson.databind.node.ObjectNode;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonCreditsIT {
  private static final Logger LOGGER = Logger.getLogger(PersonCreditsIT.class);
  
  private Long personId = 1L;

  @Test
  @Order(1)
  void testSavePerson() throws IOException, InterruptedException {
    var command = SavePerson.builder()
        .tmdbId(3)
        .name("Harrison Ford")
        .birthDate(LocalDate.parse("1942-07-13"))
        .birthPlace("Chicago, Illinois, USA")
        .gender(Gender.MALE)
        .profile(TestHelper.image("019e6228-f9af-7427-b909-090453853b6b.jpg"))
        .biography("Legendary Hollywood Icon Harrison Ford was born on July 13, 1942 in Chicago, Illinois. His family history includes a strong lineage of actors, radio personalities, and models. Ford attended public high school in Park Ridge, Illinois where he was a member of the school Radio Station WMTH. Ford worked as the lead voice for sports reporting at WMTH for several years. Acting wasn't a major interest to Ford until his junior year at Ripon College when he first took an acting class. Ford's career started in 1964 when he travelled to California in search of a voice-over job. He never received that position, but instead signed a contract with Columbia Pictures where he earned $150 weekly to play small fill in roles in various films.\n\nThrough the '60s Ford worked on several TV shows including Gunsmoke, Ironside, Kung Fu, and American Style. It wasn't until 1967 that he received his first credited role in the Western film, A Time for Killing. Dissatisfied with the meager roles he was being offered, Ford took a hiatus from acting to work as a self-employed carpenter. This seemingly odd diversion turned out to be a blessing in disguise for Harrison's acting career when he was soon hired by famous film producer George Lucas. This was a turning point in Ford's life that led to him be casted in milestone roles such as Han Solo and Indiana Jones.\n\nSince his most famous roles in the original Star Wars trilogy and Raiders of the Lost Ark, Ford has appeared in over 40 films. Many criticize his late-career work, saying his performances have been lackluster, leading to commercially disappointing films. Ford has always worked hard to protect his off-screen private life, keeping details about his children and marriages quiet. He has a total of five children including one recent adoption with third and current wife Calista Flockhart. In addition to acting, Ford is passionate about environmental conservation, aviation, and archeology.")        
        .build();
    var request = HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(TestHelper.OBJECT_MAPPER.writeValueAsString(command)))
        .uri(UriBuilder.fromUri(TestHelper.PEOPLE_URL).build())
        .build(); 
    var start = Instant.now();
    var response = TestHelper.HTTP_CLIENT.send(request, BodyHandlers.ofString());
    var et = Duration.between(start, Instant.now()).toMillis();
    assertEquals(200, response.statusCode(), "Server failed with response: " + response.body());   
    var person = TestHelper.OBJECT_MAPPER.readValue(response.body(), PersonDto.class);
    personId = person.id();
    LOGGER.infof("Saved Harrison Ford in %d ms", et);       
  } 
  
  @Test
  @Order(2)
  void testSaveBladeRunner() throws IOException, InterruptedException {
    var credits = new SaveMovie.Credits(
        List.of(
            new SaveMovie.CastCredit("52fe4214c3a36847f800259f", 3, "Harrison Ford", Gender.MALE, "/pjBMJVPpcZK23Vt1nzr1zEBTWrP.jpg", "Deckard", 0)),
        List.of());  
    
    var command = SaveMovie.builder()
        .tmdbId(78)
        .title("Blade Runner")
        .releaseDate(LocalDate.parse("1982-06-25"))
        .score(7.938f)
        .status(ShowStatus.RELEASED)
        .runtime(118)
        .budget(63000000L)
        .revenue(41722424L)
        .originalLanguage("en")
        .backdrop(TestHelper.image("019f1b50-9f18-77c5-86c6-118ae4ad6492.jpg"))
        .poster(TestHelper.image("019f1b50-9fc3-71f8-9302-1f79e7cf76f0.jpg"))
        .tagline("Man has made his match... now it's his problem.")
        .overview("In the smog-choked dystopian Los Angeles of 2019, blade runner Rick Deckard is called out of retirement to terminate a quartet of replicants who have escaped to Earth seeking their creator for a way to extend their short life spans.")
        .credits(credits)
        .build();
    var request = HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(TestHelper.OBJECT_MAPPER.writeValueAsString(command)))
        .uri(UriBuilder.fromUri(TestHelper.MOVIES_URL).build())
        .build();    
    var start = Instant.now();
    var response = TestHelper.HTTP_CLIENT.send(request, BodyHandlers.ofString());
    var et = Duration.between(start, Instant.now()).toMillis();
    assertEquals(200, response.statusCode(), "Server failed with response: " + response.body());
    LOGGER.infof("Saved Blade Runner in %d ms", et);
  }
  
  @Test
  @Order(3)
  void testSaveK19() throws IOException, InterruptedException {
    var credits = new SaveMovie.Credits(
        List.of(
            new SaveMovie.CastCredit("52fe44b2c3a36847f80a4f6f", 3, "Harrison Ford", Gender.MALE, "/pjBMJVPpcZK23Vt1nzr1zEBTWrP.jpg", "Alexei Vostrikov", 0)),
        List.of(
            new CrewCredit("52fe44b2c3a36847f80a500f", 3, "Harrison Ford", Gender.MALE, "/pjBMJVPpcZK23Vt1nzr1zEBTWrP.jpg", "Executive Producer")));  
    
    var command = SaveMovie.builder()
        .tmdbId(8665)
        .title("K-19: The Widowmaker")
        .releaseDate(LocalDate.parse("2002-07-19"))
        .score(6.571f)
        .status(ShowStatus.RELEASED)
        .runtime(138)
        .budget(100000000L)
        .revenue(65700000L)
        .originalLanguage("en")
        .backdrop(TestHelper.image("019f1b49-e1da-7040-9215-19e010ef8d5f.jpg"))
        .poster(TestHelper.image("019f1b49-e2fd-7571-84d0-6f8c0c380669.jpg"))
        .tagline("Fate has found its hero.")
        .overview("When Russia's first nuclear submarine malfunctions on its maiden voyage, the crew must race to save the ship and prevent a nuclear disaster.")
        .credits(credits)
        .build();
    var request = HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(TestHelper.OBJECT_MAPPER.writeValueAsString(command)))
        .uri(UriBuilder.fromUri(TestHelper.MOVIES_URL).build())
        .build();    
    var start = Instant.now();
    var response = TestHelper.HTTP_CLIENT.send(request, BodyHandlers.ofString());
    var et = Duration.between(start, Instant.now()).toMillis();
    assertEquals(200, response.statusCode(), "Server failed with response: " + response.body());
    LOGGER.infof("Saved K-19: The Widowmaker in %d ms", et);
  }
  
  @Test
  @Order(4)
  void testFindHarrisonFord() throws IOException, InterruptedException {
    var query = """
        query {
          person(id: %d) { 
            id tmdbId name birthDate deathDate gender profile birthPlace biography
            credits {
              cast {
                ... on PersonMovieCastCredit { creditId title score releaseDate character }
              }
              crew {
                ... on PersonMovieCrewCredit { creditId title score releaseDate job }
              }
            }            
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
    
    var node = (ObjectNode) root.path("data").path("person");
    var creditsNode = node.remove("credits"); 
    var person = TestHelper.OBJECT_MAPPER.treeToValue(node, PersonDto.class);
    assertEquals(personId, person.id());
    assertEquals(3, person.tmdbId());
    assertEquals("Harrison Ford", person.name());
    assertEquals("1942-07-13", person.birthDate().toString());
    assertNull(person.deathDate());
    assertEquals(Gender.MALE, person.gender());
    assertEquals("Chicago, Illinois, USA", person.birthPlace());
    assertEquals("Legendary Hollywood Icon Harrison Ford was born on July 13, 1942 in Chicago, Illinois. His family history includes a strong lineage of actors, radio personalities, and models. Ford attended public high school in Park Ridge, Illinois where he was a member of the school Radio Station WMTH. Ford worked as the lead voice for sports reporting at WMTH for several years. Acting wasn't a major interest to Ford until his junior year at Ripon College when he first took an acting class. Ford's career started in 1964 when he travelled to California in search of a voice-over job. He never received that position, but instead signed a contract with Columbia Pictures where he earned $150 weekly to play small fill in roles in various films.\n\nThrough the '60s Ford worked on several TV shows including Gunsmoke, Ironside, Kung Fu, and American Style. It wasn't until 1967 that he received his first credited role in the Western film, A Time for Killing. Dissatisfied with the meager roles he was being offered, Ford took a hiatus from acting to work as a self-employed carpenter. This seemingly odd diversion turned out to be a blessing in disguise for Harrison's acting career when he was soon hired by famous film producer George Lucas. This was a turning point in Ford's life that led to him be casted in milestone roles such as Han Solo and Indiana Jones.\n\nSince his most famous roles in the original Star Wars trilogy and Raiders of the Lost Ark, Ford has appeared in over 40 films. Many criticize his late-career work, saying his performances have been lackluster, leading to commercially disappointing films. Ford has always worked hard to protect his off-screen private life, keeping details about his children and marriages quiet. He has a total of five children including one recent adoption with third and current wife Calista Flockhart. In addition to acting, Ford is passionate about environmental conservation, aviation, and archeology.", person.biography());        
    
    var cast = creditsNode.path("cast");
    assertEquals(2, cast.size());
    assertEquals("Blade Runner", cast.get(0).path("title").asText());
    assertEquals("K-19: The Widowmaker", cast.get(1).path("title").asText());
    
    var crew = creditsNode.path("crew");
    assertEquals(1, crew.size());
    assertEquals("K-19: The Widowmaker", crew.get(0).path("title").asText());    
    LOGGER.infof("Found Harrison Ford in %d ms", et);    
  }  
}
