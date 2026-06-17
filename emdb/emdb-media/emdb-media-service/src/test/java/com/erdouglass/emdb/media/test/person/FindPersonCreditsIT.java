package com.erdouglass.emdb.media.test.person;

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

import com.erdouglass.emdb.media.Gender;
import com.erdouglass.emdb.media.command.SaveMovie;
import com.erdouglass.emdb.media.command.SavePerson;
import com.erdouglass.emdb.media.command.SaveSeries;
import com.erdouglass.emdb.media.command.SaveSeries.CrewCredit;
import com.erdouglass.emdb.media.command.SaveSeries.CastCredit.Role;
import com.erdouglass.emdb.media.command.SaveSeries.CrewCredit.Job;
import com.erdouglass.emdb.media.query.PersonResponse;
import com.erdouglass.emdb.media.show.SeriesType;
import com.erdouglass.emdb.media.show.ShowStatus;
import com.erdouglass.emdb.media.test.TestHelper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FindPersonCreditsIT {
  private static final Logger LOGGER = Logger.getLogger(FindPersonCreditsIT.class);
  
  private Long personId;
  
  @Test
  @Order(1)
  void testSaveHarrisonFord() throws IOException, InterruptedException {
    var command = SavePerson.builder()
        .tmdbId(3)
        .name("Harrison Ford")
        .birthDate(LocalDate.parse("1942-07-13"))
        .birthPlace("Chicago, Illinois, USA")
        .gender(Gender.MALE)
        .profile(TestHelper.image("/pjBMJVPpcZK23Vt1nzr1zEBTWrP.jpg", "019e6228-f9af-7427-b909-090453853b6b.jpg"))
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
    
    var person = TestHelper.OBJECT_MAPPER.readValue(response.body(), PersonResponse.class);
    personId = person.id();
    assertEquals(3, person.tmdbId());
    assertEquals("Harrison Ford", person.name());
    assertEquals("1942-07-13", person.birthDate().toString());
    assertNull(person.deathDate());
    assertEquals(Gender.MALE, person.gender());
    assertEquals("Chicago, Illinois, USA", person.birthPlace());
    assertEquals("Legendary Hollywood Icon Harrison Ford was born on July 13, 1942 in Chicago, Illinois. His family history includes a strong lineage of actors, radio personalities, and models. Ford attended public high school in Park Ridge, Illinois where he was a member of the school Radio Station WMTH. Ford worked as the lead voice for sports reporting at WMTH for several years. Acting wasn't a major interest to Ford until his junior year at Ripon College when he first took an acting class. Ford's career started in 1964 when he travelled to California in search of a voice-over job. He never received that position, but instead signed a contract with Columbia Pictures where he earned $150 weekly to play small fill in roles in various films.\n\nThrough the '60s Ford worked on several TV shows including Gunsmoke, Ironside, Kung Fu, and American Style. It wasn't until 1967 that he received his first credited role in the Western film, A Time for Killing. Dissatisfied with the meager roles he was being offered, Ford took a hiatus from acting to work as a self-employed carpenter. This seemingly odd diversion turned out to be a blessing in disguise for Harrison's acting career when he was soon hired by famous film producer George Lucas. This was a turning point in Ford's life that led to him be casted in milestone roles such as Han Solo and Indiana Jones.\n\nSince his most famous roles in the original Star Wars trilogy and Raiders of the Lost Ark, Ford has appeared in over 40 films. Many criticize his late-career work, saying his performances have been lackluster, leading to commercially disappointing films. Ford has always worked hard to protect his off-screen private life, keeping details about his children and marriages quiet. He has a total of five children including one recent adoption with third and current wife Calista Flockhart. In addition to acting, Ford is passionate about environmental conservation, aviation, and archeology.", person.biography());    
    LOGGER.infof("Saved Harrison Ford in %d ms", et);       
  }
  
  @Test
  @Order(2)
  void testSaveMovie() throws IOException, InterruptedException {
    var credits = new SaveMovie.Credits(
        List.of(
            new SaveMovie.CastCredit("52fe4214c3a36847f800259f", 3, "Harrison Ford", Gender.MALE, "/pjBMJVPpcZK23Vt1nzr1zEBTWrP.jpg", "Deckard", 0),
            new SaveMovie.CastCredit("52fe4214c3a36847f80025a3", 585, "Rutger Hauer", Gender.MALE,  "/xyag0LbjocTwx2Ht7B3RofvCNMT.jpg", "Batty", 1)),
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
        .backdrop(TestHelper.image("/kuPpElzfYnzsCye0hF8EbJSrvwo.jpg", "019e5c92-5a24-7517-8b7a-3734166ad76a.jpg"))
        .poster(TestHelper.image("/n8V61f1v7idya4WJzGEJNoIp9iL.jpg", "019e5c8d-efdc-7687-b6c7-a6e822fb6d6d.jpg"))
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
  void testSaveSeries() throws IOException, InterruptedException {
    var credits = new SaveSeries.Credits(
        List.of(
            new SaveSeries.CastCredit(41088, "Jason Segel", Gender.MALE, "/aG6tVNSbl1YEjN65G3luFYnWbUM.jpg", List.of(
                new Role("61672aea8fdda900623b4a97", "Jimmy", 33)), 0),
            new SaveSeries.CastCredit(3, "Harrison Ford", Gender.FEMALE, "/pjBMJVPpcZK23Vt1nzr1zEBTWrP.jpg", List.of(
                new Role("624b1895e8a3e10062c89f87", "Dr. Paul Rhodes", 33)), 1)),
        List.of(
            new CrewCredit(3, "Harrison Fors", Gender.MALE, "/pjBMJVPpcZK23Vt1nzr1zEBTWrP.jpg", List.of(
                new Job("5256bdcd19c2956ff60020be", "Writer", 1)))));
    
    var command = SaveSeries.builder()
        .tmdbId(136311)
        .title("Shrinking")
        .score(8.015f)
        .status(ShowStatus.RETURNING_SERIES)
        .type(SeriesType.SCRIPTED)
        .homepage("https://tv.apple.com/show/umc.cmc.apzybj6eqf6pzccd97kev7bs")
        .originalLanguage("en")
        .backdrop(TestHelper.image("/kuPpElzfYnzsCye0hF8EbJSrvwo.jpg", "019e5c92-5a24-7517-8b7a-3734166ad76a.jpg"))
        .poster(TestHelper.image("/n8V61f1v7idya4WJzGEJNoIp9iL.jpg", "019e5c8d-efdc-7687-b6c7-a6e822fb6d6d.jpg"))
        .overview("Jimmy is struggling to grieve the loss of his wife while being a dad, friend, and therapist. He decides to try a new approach with everyone in his path: unfiltered, brutal honesty. Will it make things better—or unleash uproarious chaos?")
        .credits(credits)
        .build();
    var request = HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(TestHelper.OBJECT_MAPPER.writeValueAsString(command)))
        .uri(UriBuilder.fromUri(TestHelper.SERIES_URL).build())
        .build();    
    var start = Instant.now();
    var response = TestHelper.HTTP_CLIENT.send(request, BodyHandlers.ofString());
    var et = Duration.between(start, Instant.now()).toMillis();
    assertEquals(200, response.statusCode(), "Server failed with response: " + response.body());
    LOGGER.infof("Saved Shrinking in %d ms", et);    
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
                ... on PersonSeriesCastCredit { creditId title score firstAirDate roles { character episodeCount } }
              }
              crew {
                ... on PersonMovieCrewCredit { creditId title score releaseDate job }
                ... on PersonSeriesCrewCredit { creditId title score firstAirDate jobs { title episodeCount } }
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
    
    var node = (ObjectNode) root.path("data").path("findPersonById");
    var creditsNode = node.remove("credits"); 
    var person = TestHelper.OBJECT_MAPPER.treeToValue(node, PersonResponse.class);
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
    assertEquals("Shrinking", cast.get(0).path("title").asText());
    assertEquals("Blade Runner", cast.get(1).path("title").asText());
    LOGGER.infof("Found Harrison Ford in %d ms", et);    
  }
}
