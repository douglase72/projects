package com.erdouglass.emdb.media.test.series;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.erdouglass.emdb.media.Image;
import com.erdouglass.emdb.media.series.SaveSeries;
import com.erdouglass.emdb.media.series.SeriesResponse;
import com.erdouglass.emdb.media.series.SeriesType;
import com.erdouglass.emdb.media.show.ShowStatus;
import com.erdouglass.emdb.media.test.TestHelper;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TheSimpsonsCrudIT {
  private static final Logger LOGGER = Logger.getLogger(TheSimpsonsCrudIT.class);
  
  @Test
  @Order(1)
  void testSaveSeries() throws IOException, InterruptedException {
    var command = SaveSeries.builder()
        .tmdbId(456)
        .title("The Simpsons")
        .score(8.015f)
        .status(ShowStatus.RETURNING_SERIES)
        .type(SeriesType.SCRIPTED)
        .homepage("http://www.thesimpsons.com/")
        .originalLanguage("en")
        .backdrop(image("/kuPpElzfYnzsCye0hF8EbJSrvwo.jpg", "019e5c92-5a24-7517-8b7a-3734166ad76a.jpg"))
        .poster(image("/n8V61f1v7idya4WJzGEJNoIp9iL.jpg", "019e5c8d-efdc-7687-b6c7-a6e822fb6d6d.jpg"))
        .overview("Set in Springfield, the average American town, the show focuses on the antics and everyday adventures of the Simpson family; Homer, Marge, Bart, Lisa and Maggie, as well as a virtual cast of thousands. Since the beginning, the series has been a pop culture icon, attracting hundreds of celebrities to guest star. The show has also made name for itself in its fearless satirical take on politics, media and American life in general.")
        .build();
    var request = HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(TestHelper.OBJECT_MAPPER.writeValueAsString(command)))
        .uri(UriBuilder.fromUri(TestHelper.SERIES_URL).build())
        .build();    
    var start = Instant.now();
    var response = TestHelper.HTTP_CLIENT.send(request, BodyHandlers.ofString());
    var et = Duration.between(start, Instant.now()).toMillis();
    assertEquals(201, response.statusCode(), "Server failed with response: " + response.body());
    
    var series = TestHelper.OBJECT_MAPPER.readValue(response.body(), SeriesResponse.class);
    assertEquals(456, series.tmdbId());
    assertEquals("The Simpsons", series.title());
    assertEquals(8.015f, series.score());
    assertEquals(ShowStatus.RETURNING_SERIES, series.status());
    assertEquals(SeriesType.SCRIPTED, series.type());
    assertEquals("http://www.thesimpsons.com/", series.homepage());
    assertEquals("Set in Springfield, the average American town, the show focuses on the antics and everyday adventures of the Simpson family; Homer, Marge, Bart, Lisa and Maggie, as well as a virtual cast of thousands. Since the beginning, the series has been a pop culture icon, attracting hundreds of celebrities to guest star. The show has also made name for itself in its fearless satirical take on politics, media and American life in general.", series.overview());    
    LOGGER.infof("Saved The Simpsons in %d ms", et);    
  }
  
  private Image image(String tmdbName, String image) throws IOException {
    var dir = "/home/erdouglass/projects/emdb/emdb-media/emdb-media-service/test-data";
    byte[] data = Files.readAllBytes(Path.of(dir, image));
    return Image.builder().tmdbName(tmdbName).data(data).build();
  }
}
