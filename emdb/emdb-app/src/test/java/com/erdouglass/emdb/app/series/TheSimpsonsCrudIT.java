package com.erdouglass.emdb.app.series;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.Instant;

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
import com.erdouglass.emdb.media.SaveSeries;
import com.erdouglass.emdb.media.SeriesType;
import com.erdouglass.emdb.media.show.ShowStatus;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TheSimpsonsCrudIT {
  private static final Logger LOGGER = Logger.getLogger(TheSimpsonsCrudIT.class);
  
  private Long seriesId;
  
  @Test
  @Order(1)
  void testSaveSeries() throws IOException, InterruptedException {
     var command = SaveSeries.builder()
        .externalId(456)
        .title("The Simpsons")
        .score(BigDecimal.valueOf(8.015))
        .status(ShowStatus.RETURNING_SERIES)
        .type(SeriesType.SCRIPTED)
        .homepage("http://www.thesimpsons.com/")
        .originalLanguage("en")
        .backdrop(TestHelper.image("019e5c92-5a24-7517-8b7a-3734166ad76a.jpg"))
        .poster(TestHelper.image("019e5c8d-efdc-7687-b6c7-a6e822fb6d6d.jpg"))
        .overview("Set in Springfield, the average American town, the show focuses on the antics and everyday adventures of the Simpson family; Homer, Marge, Bart, Lisa and Maggie, as well as a virtual cast of thousands. Since the beginning, the series has been a pop culture icon, attracting hundreds of celebrities to guest star. The show has also made name for itself in its fearless satirical take on politics, media and American life in general.")
        .build();
    var request = HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(TestHelper.OBJECT_MAPPER.writeValueAsString(command)))
        .uri(UriBuilder.fromUri(TestHelper.MEDIA_URL).path("series").build())
        .build();    
    var start = Instant.now();
    var response = TestHelper.HTTP_CLIENT.send(request, BodyHandlers.ofString());
    var et = Duration.between(start, Instant.now()).toMillis();
    assertEquals(200, response.statusCode(), "Server failed with response: " + response.body());    
    var result = TestHelper.OBJECT_MAPPER.readValue(response.body(), SaveResult.class);
    seriesId = result.id();
    LOGGER.infof("Saved The Simpsons in %d ms", et);    
  }
}
