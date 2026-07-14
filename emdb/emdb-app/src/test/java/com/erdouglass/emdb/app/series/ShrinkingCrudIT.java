package com.erdouglass.emdb.app.series;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
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
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import com.erdouglass.emdb.app.TestHelper;
import com.erdouglass.emdb.media.Gender;
import com.erdouglass.emdb.media.SaveResult;
import com.erdouglass.emdb.media.SaveSeries;
import com.erdouglass.emdb.media.SaveSeries.CastCredit;
import com.erdouglass.emdb.media.SaveSeries.CastCredit.Role;
import com.erdouglass.emdb.media.SaveSeries.Credits;
import com.erdouglass.emdb.media.SaveSeries.CrewCredit;
import com.erdouglass.emdb.media.SaveSeries.CrewCredit.Job;
import com.erdouglass.emdb.media.SeriesType;
import com.erdouglass.emdb.media.show.ShowStatus;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ShrinkingCrudIT {
  private static final Logger LOGGER = Logger.getLogger(ShrinkingCrudIT.class);
  
  private Long seriesId;
  
  @Test
  @Order(1)
  void testSaveSeries() throws IOException, InterruptedException {
    var credits = new Credits(
        List.of(
            new CastCredit(3L, "Harrison Ford", Gender.MALE, "/pjBMJVPpcZK23Vt1nzr1zEBTWrP.jpg", List.of(
                new Role("624b1895e8a3e10062c89f87", "Dr. Paul Rhodes", 33)), 1)), 
        List.of(
            new CrewCredit(3L, "Harrison Ford", Gender.MALE, "/pjBMJVPpcZK23Vt1nzr1zEBTWrP.jpg", List.of(
                new Job("5256bdcd19c2956ff60020be", "Writer", 1)))));
    
    var command = SaveSeries.builder()
        .externalId(136311)
        .title("Shrinking")
        .score(BigDecimal.valueOf(8.015))
        .status(ShowStatus.RETURNING_SERIES)
        .type(SeriesType.SCRIPTED)
        .homepage("https://tv.apple.com/show/umc.cmc.apzybj6eqf6pzccd97kev7bs")
        .originalLanguage("en")
        .backdrop(TestHelper.image("019e5c92-5a24-7517-8b7a-3734166ad76a.jpg"))
        .poster(TestHelper.image("019e5c8d-efdc-7687-b6c7-a6e822fb6d6d.jpg"))
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
    var result = TestHelper.OBJECT_MAPPER.readValue(response.body(), SaveResult.class);
    seriesId = result.id();
    LOGGER.infof("Saved Shrinking in %d ms", et);    
  }

  @Test
  @Order(2)
  void testFindSeries() throws IOException, InterruptedException {
    var query = """
        query {
          series(id: %d) { 
            id title firstAirDate lastAirDate score status type
            backdrop poster homepage originalLanguage tagline overview                            
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
    
    var series = root.path("data").path("series");
    assertEquals(seriesId, series.path("id").asLong());
    assertEquals("Shrinking", series.path("title").asText());
    assertEquals(8.015, series.path("score").asDouble(), 0.001);
    assertEquals("RETURNING_SERIES", series.path("status").asText());
    assertEquals("SCRIPTED", series.path("type").asText());
    assertEquals("https://tv.apple.com/show/umc.cmc.apzybj6eqf6pzccd97kev7bs", series.path("homepage").asText());
    assertEquals("en", series.path("originalLanguage").asText());
    assertEquals("Jimmy is struggling to grieve the loss of his wife while being a dad, friend, and therapist. He decides to try a new approach with everyone in his path: unfiltered, brutal honesty. Will it make things better—or unleash uproarious chaos?", series.path("overview").asText());
    LOGGER.infof("Found Shrinking in %d ms", et);    
  }  
}
