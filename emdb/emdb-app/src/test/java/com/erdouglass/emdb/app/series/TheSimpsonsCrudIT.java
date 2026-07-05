package com.erdouglass.emdb.app.series;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.erdouglass.emdb.app.TestHelper;
import com.erdouglass.emdb.media.person.Gender;
import com.erdouglass.emdb.media.series.SaveSeries;
import com.erdouglass.emdb.media.series.SaveSeries.CastCredit;
import com.erdouglass.emdb.media.series.SaveSeries.CastCredit.Role;
import com.erdouglass.emdb.media.series.SaveSeries.Credits;
import com.erdouglass.emdb.media.series.SaveSeries.CrewCredit;
import com.erdouglass.emdb.media.series.SaveSeries.CrewCredit.Job;
import com.erdouglass.emdb.media.series.SeriesDto;
import com.erdouglass.emdb.media.series.SeriesType;
import com.erdouglass.emdb.media.show.ShowStatus;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TheSimpsonsCrudIT {
  private static final Logger LOGGER = Logger.getLogger(TheSimpsonsCrudIT.class);
  
  private Long seriesId;
  
  @Test
  @Order(1)
  void testSaveSeries() throws IOException, InterruptedException {
    var credits = new Credits(
        List.of(
            new CastCredit(198, "Dan Castellaneta", Gender.MALE, "/AmeqWhP4A46AWkM4kVphg6jOTQX.jpg", List.of(
                new Role("5256bdc319c2956ff600157c", "Homer Simpson / Abe Simpson / Barney Gumble / Krusty (voice)", 801)), 0),
            new CastCredit(6009, "Pamela Hayden", Gender.FEMALE, "/mPMtuVB6AEulRhlfn69y5RvgmNT.jpg", List.of(
                new Role("66d03a008378b206bc8842b1", "Milhouse Van Houten (voice)", 80),
                new Role("66d16e86bf547fec04a7ea89", "Milhouse Van Houten / Jimbo Jones (voice)", 39),
                new Role("644e6c604d23dd20d792c8d1", "Milhouse (voice)", 59)), 1768)), 
        List.of(
            new CrewCredit(5741, "Matt Groening", Gender.MALE, "/2HmAw3AN93DGESPi3ibLZgBa8cT.jpg", List.of(
                new Job("5256bdcd19c2956ff60020be", "Executive Producer", 673),
                new Job("60c1334dd34eb30040a2824e", "Character Designer", 1)))));
    
    var command = SaveSeries.builder()
        .tmdbId(456)
        .title("The Simpsons")
        .score(8.015f)
        .status(ShowStatus.RETURNING_SERIES)
        .type(SeriesType.SCRIPTED)
        .homepage("http://www.thesimpsons.com/")
        .originalLanguage("en")
        .backdrop(TestHelper.image("019e5c92-5a24-7517-8b7a-3734166ad76a.jpg"))
        .poster(TestHelper.image("019e5c8d-efdc-7687-b6c7-a6e822fb6d6d.jpg"))
        .overview("Set in Springfield, the average American town, the show focuses on the antics and everyday adventures of the Simpson family; Homer, Marge, Bart, Lisa and Maggie, as well as a virtual cast of thousands. Since the beginning, the series has been a pop culture icon, attracting hundreds of celebrities to guest star. The show has also made name for itself in its fearless satirical take on politics, media and American life in general.")
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
    
    var series = TestHelper.OBJECT_MAPPER.readValue(response.body(), SeriesDto.class);
    seriesId = series.id();
    LOGGER.infof("Saved The Simpsons in %d ms", et);    
  }
  
  @Test
  @Order(2)
  void testFindSeries() throws IOException, InterruptedException {
    var query = """
        query {
          series(id: %d) { 
            id tmdbId title firstAirDate lastAirDate score status type
            backdrop poster homepage originalLanguage tagline overview 
            credits {
              cast { id name gender profile roles { creditId character episodeCount } order }
              crew { id name gender profile jobs { creditId title episodeCount } }
            }                       
          }
        }
        """.formatted(seriesId);
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
    
    var series = TestHelper.OBJECT_MAPPER.treeToValue(root.path("data").path("series"), SeriesDto.class);
    assertEquals(seriesId, series.id());
    assertEquals(456, series.tmdbId());
    assertEquals("The Simpsons", series.title());
    assertEquals(8.015f, series.score());
    assertEquals(ShowStatus.RETURNING_SERIES, series.status());
    assertEquals(SeriesType.SCRIPTED, series.type());
    assertEquals("http://www.thesimpsons.com/", series.homepage());
    assertEquals("Set in Springfield, the average American town, the show focuses on the antics and everyday adventures of the Simpson family; Homer, Marge, Bart, Lisa and Maggie, as well as a virtual cast of thousands. Since the beginning, the series has been a pop culture icon, attracting hundreds of celebrities to guest star. The show has also made name for itself in its fearless satirical take on politics, media and American life in general.", series.overview());    

    var cast = series.credits().cast();
    assertEquals(2, cast.size());
    assertEquals("Dan Castellaneta", cast.get(0).name());
    assertEquals("Pamela Hayden", cast.get(1).name());
    var roles = cast.get(1).roles();
    assertEquals("Milhouse Van Houten (voice)", roles.get(0).character());
    assertEquals("Milhouse (voice)", roles.get(1).character());
    assertEquals("Milhouse Van Houten / Jimbo Jones (voice)", roles.get(2).character());
    
    var crew = series.credits().crew();
    assertEquals(1, crew.size());
    assertEquals("Executive Producer", crew.get(0).jobs().get(0).title());
    assertEquals("Character Designer", crew.get(0).jobs().get(1).title());    
    LOGGER.infof("Found The Simpsons in %d ms", et);    
  }  
}
