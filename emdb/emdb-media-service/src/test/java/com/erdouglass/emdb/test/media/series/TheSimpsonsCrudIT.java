package com.erdouglass.emdb.test.media.series;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import com.erdouglass.emdb.common.SeriesType;
import com.erdouglass.emdb.common.ShowStatus;
import com.erdouglass.emdb.common.comand.SaveSeries;
import com.erdouglass.emdb.common.comand.UpdateSeries;
import com.erdouglass.emdb.common.query.SeriesDto;
import com.erdouglass.emdb.test.media.AbstractTest;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TheSimpsonsCrudIT extends AbstractTest {
  private static final Logger LOGGER = Logger.getLogger(TheSimpsonsCrudIT.class);
  private static final UUID BACKDROP = UUID.fromString("fca95726-f1f9-492b-b0c4-bc126792fd48");
  private static final UUID POSTER = UUID.fromString("72a875ef-2764-4128-a3a9-54dbdf210035");
  
  private Long seriesId;  

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
        .backdrop(BACKDROP)
        .poster(POSTER)
        .overview("Set in Springfield, the average American town, the show focuses on the antics and everyday adventures of the Simpson family; Homer, Marge, Bart, Lisa and Maggie, as well as a virtual cast of thousands. Since the beginning, the series has been a pop culture icon, attracting hundreds of celebrities to guest star. The show has also made name for itself in its fearless satirical take on politics, media and American life in general.")
        .build();
    var request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(SERIES_URL).build())
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command))).build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    var series = OBJECT_MAPPER.readValue(response.body(), SeriesDto.class);
    seriesId = series.id();
    assertEquals(200, response.statusCode());
    assertEquals(456, series.tmdbId());
    assertEquals("The Simpsons", series.title());
    assertEquals(8.015f, series.score());
    assertEquals(ShowStatus.RETURNING_SERIES, series.status());
    assertEquals(SeriesType.SCRIPTED, series.type());
    assertEquals("http://www.thesimpsons.com/", series.homepage());
    assertEquals("fca95726-f1f9-492b-b0c4-bc126792fd48.jpg", series.backdrop());
    assertEquals("72a875ef-2764-4128-a3a9-54dbdf210035.jpg", series.poster());
    assertEquals("Set in Springfield, the average American town, the show focuses on the antics and everyday adventures of the Simpson family; Homer, Marge, Bart, Lisa and Maggie, as well as a virtual cast of thousands. Since the beginning, the series has been a pop culture icon, attracting hundreds of celebrities to guest star. The show has also made name for itself in its fearless satirical take on politics, media and American life in general.", series.overview());    
    LOGGER.infof("Saved series %d in %d ms", seriesId, et);
  }
  
  @Test
  @Order(2)
  void testFindSeries() throws IOException, InterruptedException {
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(SERIES_URL).path(seriesId.toString()).build())
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    var series = OBJECT_MAPPER.readValue(response.body(), SeriesDto.class);
    assertEquals(200, response.statusCode());
    assertEquals(456, series.tmdbId());
    assertEquals("The Simpsons", series.title());
    assertEquals(8.015f, series.score());
    assertEquals(ShowStatus.RETURNING_SERIES, series.status());
    assertEquals(SeriesType.SCRIPTED, series.type());
    assertEquals("http://www.thesimpsons.com/", series.homepage());
    assertEquals("fca95726-f1f9-492b-b0c4-bc126792fd48.jpg", series.backdrop());
    assertEquals("72a875ef-2764-4128-a3a9-54dbdf210035.jpg", series.poster());
    assertEquals("Set in Springfield, the average American town, the show focuses on the antics and everyday adventures of the Simpson family; Homer, Marge, Bart, Lisa and Maggie, as well as a virtual cast of thousands. Since the beginning, the series has been a pop culture icon, attracting hundreds of celebrities to guest star. The show has also made name for itself in its fearless satirical take on politics, media and American life in general.", series.overview());    
    LOGGER.infof("Found series %d details in: %d ms", seriesId, et);
  }
  
  @Test
  @Order(3)
  void testUpdateSeries() throws IOException, InterruptedException {
    var command = UpdateSeries.builder()
        .title("The Simpsons")
        .score(6.5f)
        .status(ShowStatus.RETURNING_SERIES)
        .type(SeriesType.SCRIPTED)
        .homepage("http://www.thesimpsons.com/")
        .originalLanguage("en")
        .backdrop(BACKDROP)
        .poster(POSTER)
        .overview("Set in Springfield, the average American town, the show focuses on the antics and everyday adventures of the Simpson family; Homer, Marge, Bart, Lisa and Maggie, as well as a virtual cast of thousands. Since the beginning, the series has been a pop culture icon, attracting hundreds of celebrities to guest star. The show has also made name for itself in its fearless satirical take on politics, media and American life in general.")
        .build();
    var request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(SERIES_URL).path(seriesId.toString()).build())
        .PUT(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command))).build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    var series = OBJECT_MAPPER.readValue(response.body(), SeriesDto.class);
    seriesId = series.id();
    assertEquals(200, response.statusCode());
    assertEquals(456, series.tmdbId());
    assertEquals("The Simpsons", series.title());
    assertEquals(6.5f, series.score());
    assertEquals(ShowStatus.RETURNING_SERIES, series.status());
    assertEquals(SeriesType.SCRIPTED, series.type());
    assertEquals("http://www.thesimpsons.com/", series.homepage());
    assertEquals("fca95726-f1f9-492b-b0c4-bc126792fd48.jpg", series.backdrop());
    assertEquals("72a875ef-2764-4128-a3a9-54dbdf210035.jpg", series.poster());
    assertEquals("Set in Springfield, the average American town, the show focuses on the antics and everyday adventures of the Simpson family; Homer, Marge, Bart, Lisa and Maggie, as well as a virtual cast of thousands. Since the beginning, the series has been a pop culture icon, attracting hundreds of celebrities to guest star. The show has also made name for itself in its fearless satirical take on politics, media and American life in general.", series.overview());    
    LOGGER.infof("Updated series %d in %d ms", seriesId, et);
  }
  
  @Test
  @Order(5)
  void testDeleteSeries() throws IOException, InterruptedException {
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(SERIES_URL).path(seriesId.toString()).build())
        .DELETE()
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(204, response.statusCode());
    LOGGER.infof("Deleted series %d in: %d ms", seriesId, et);    
  }

}
