package com.erdouglass.emdb.test.gateway.series;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import com.erdouglass.emdb.media.api.Image;
import com.erdouglass.emdb.media.api.SeriesType;
import com.erdouglass.emdb.media.api.ShowStatus;
import com.erdouglass.emdb.media.api.command.SaveSeries;
import com.erdouglass.emdb.media.api.query.SeriesDetails;
import com.erdouglass.emdb.test.gateway.AbstractTest;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SeriesLoadIT extends AbstractTest {
  private static final Logger LOGGER = Logger.getLogger(SeriesLoadIT.class);
  private static final UUID BACKDROP = UUID.fromString("019d3220-adb3-75ba-b1b2-1619de2a2fef");
  private static final UUID POSTER = UUID.fromString("019d3220-aead-702b-a997-5700e9a2076a"); 
  private static final int REQUESTS = 50;
  private static final String PROD_URL = "http://localhost/emdb/api/series";
  
  private String token;
  private Long seriesId;
  
  @BeforeAll
  void setupSecurity() throws IOException, InterruptedException {
    this.token = getAccessToken();
  }
  
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
        .backdrop(Image.of(BACKDROP, "/mPMtuVB6AEulRhlfn69y5RvgmNT.jpg"))
        .poster(Image.of(POSTER, "/zp5gxcPnxv6FsDh3l7yRZurlBRr.jpg"))
        .overview("Set in Springfield, the average American town, the show focuses on the antics and everyday adventures of the Simpson family; Homer, Marge, Bart, Lisa and Maggie, as well as a virtual cast of thousands. Since the beginning, the series has been a pop culture icon, attracting hundreds of celebrities to guest star. The show has also made name for itself in its fearless satirical take on politics, media and American life in general.")
        .build(); 
    var request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(PROD_URL).build())
        .header("Authorization", "Bearer " + token)
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command))).build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(201, response.statusCode(), "Server failed with response: " + response.body());
    
    var series = OBJECT_MAPPER.readValue(response.body(), SeriesDetails.class);
    seriesId = series.id();
    assertEquals(456, series.tmdbId());
    assertEquals("The Simpsons", series.title());
    assertEquals(8.015f, series.score());
    assertEquals(ShowStatus.RETURNING_SERIES, series.status());
    assertEquals(SeriesType.SCRIPTED, series.type());
    assertEquals("http://www.thesimpsons.com/", series.homepage());
    assertEquals("019d3220-adb3-75ba-b1b2-1619de2a2fef.jpg", series.backdrop());
    assertEquals("019d3220-aead-702b-a997-5700e9a2076a.jpg", series.poster());
    assertEquals("Set in Springfield, the average American town, the show focuses on the antics and everyday adventures of the Simpson family; Homer, Marge, Bart, Lisa and Maggie, as well as a virtual cast of thousands. Since the beginning, the series has been a pop culture icon, attracting hundreds of celebrities to guest star. The show has also made name for itself in its fearless satirical take on politics, media and American life in general.", series.overview());
    LOGGER.infof("Saved series %d in %d ms", series.id(), et);
  }
  
  @Test
  @Order(2)
  void testFindSeries() throws IOException, InterruptedException {
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(PROD_URL).path(seriesId.toString()).queryParam("append", "credits").build())
        .build();
    long s1 = System.nanoTime();
    for (int i = 0; i < REQUESTS; i++) {
      long s2 = System.nanoTime();
      var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
      long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - s2);
      assertEquals(200, response.statusCode());    
      LOGGER.infof("Found series in: %d ms", et);
    }
    LOGGER.infof("Executed %d requests in: %d ms", REQUESTS, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - s1));
  }
}
