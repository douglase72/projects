package com.erdouglass.emdb.test.gateway.series;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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

import com.erdouglass.emdb.common.Image;
import com.erdouglass.emdb.common.SeriesType;
import com.erdouglass.emdb.common.ShowStatus;
import com.erdouglass.emdb.common.comand.SaveSeries;
import com.erdouglass.emdb.common.comand.UpdateSeries;
import com.erdouglass.emdb.common.query.SeriesDetails;
import com.erdouglass.emdb.test.gateway.AbstractTest;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SeriesCrudIT extends AbstractTest {
  private static final Logger LOGGER = Logger.getLogger(SeriesCrudIT.class);
  private static final UUID BACKDROP = UUID.fromString("019d3220-adb3-75ba-b1b2-1619de2a2fef");
  private static final UUID POSTER = UUID.fromString("019d3220-aead-702b-a997-5700e9a2076a");  
  
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
    var request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(SERIES_URL).build())
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
        .uri(UriBuilder.fromUri(SERIES_URL).path(seriesId.toString()).queryParam("append", "credits").build())
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(200, response.statusCode());
    
    var series = OBJECT_MAPPER.readValue(response.body(), SeriesDetails.class);
    assertEquals(456, series.tmdbId());
    assertEquals("The Simpsons", series.title());
    assertEquals(8.015f, series.score());
    assertEquals(ShowStatus.RETURNING_SERIES, series.status());
    assertEquals(SeriesType.SCRIPTED, series.type());
    assertEquals("http://www.thesimpsons.com/", series.homepage());
    assertEquals("019d3220-adb3-75ba-b1b2-1619de2a2fef.jpg", series.backdrop());
    assertEquals("019d3220-aead-702b-a997-5700e9a2076a.jpg", series.poster());
    assertEquals("Set in Springfield, the average American town, the show focuses on the antics and everyday adventures of the Simpson family; Homer, Marge, Bart, Lisa and Maggie, as well as a virtual cast of thousands. Since the beginning, the series has been a pop culture icon, attracting hundreds of celebrities to guest star. The show has also made name for itself in its fearless satirical take on politics, media and American life in general.", series.overview());
    LOGGER.infof("Found series %d in: %d ms", seriesId, et);    
  }
  
  @Test
  @Order(3)
  void testSeriesNotFound() throws IOException, InterruptedException {
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(SERIES_URL).path("999").queryParam("append", "credits").build())
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(404, response.statusCode());
    LOGGER.infof("Series not found completed in: %d ms", et);    
  }
  
  @Test
  @Order(4)
  void testUpdateSeries() throws IOException, InterruptedException {
    var command = UpdateSeries.builder()
        .title("X")
        .score(6.6f)
        .status(ShowStatus.RUMORED)
        .type(SeriesType.DOCUMENTARY)
        .originalLanguage("en")
        .backdrop(UUID.fromString("019d3220-adb3-75ba-b1b2-1619de2a2fef"))
        .poster(UUID.fromString("019d3220-aead-702b-a997-5700e9a2076a"))
        .overview("Test overview.")
        .build(); 
    var request  = HttpRequest.newBuilder().uri(UriBuilder.fromUri(SERIES_URL).path(seriesId.toString()).build())
        .header("Authorization", "Bearer " + token)
        .PUT(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command))).build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(200, response.statusCode(), "Server failed with response: " + response.body());
    
    var series = OBJECT_MAPPER.readValue(response.body(), SeriesDetails.class);
    assertEquals(200, response.statusCode());
    assertEquals(456, series.tmdbId());
    assertEquals("X", series.title());
    assertEquals(6.6f, series.score());
    assertEquals(ShowStatus.RUMORED, series.status());
    assertEquals(SeriesType.DOCUMENTARY, series.type());
    assertEquals("019d3220-adb3-75ba-b1b2-1619de2a2fef.jpg", series.backdrop());
    assertEquals("019d3220-aead-702b-a997-5700e9a2076a.jpg", series.poster());    
    assertEquals("http://www.thesimpsons.com/", series.homepage());    
    assertEquals("en", series.originalLanguage());
    assertNull(series.tagline());
    assertEquals("Test overview.", series.overview());    
    LOGGER.infof("Updated series %d in %d ms", series.id(), et);      
  }
  
  @Test
  @Order(5)
  void testDeleteSeries() throws IOException, InterruptedException {
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(SERIES_URL).path(seriesId.toString()).build())
        .header("Authorization", "Bearer " + token)
        .DELETE()
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(204, response.statusCode());
    LOGGER.infof("Deleted series %d in: %d ms", seriesId, et);    
  }
}
