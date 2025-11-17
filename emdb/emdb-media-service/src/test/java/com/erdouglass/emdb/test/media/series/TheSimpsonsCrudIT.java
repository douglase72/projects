package com.erdouglass.emdb.test.media.series;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import com.erdouglass.emdb.common.ShowStatus;
import com.erdouglass.emdb.common.command.SeriesCreateCommand;
import com.erdouglass.emdb.common.command.SeriesUpdateCommand;
import com.erdouglass.emdb.common.query.SeriesDto;
import com.erdouglass.emdb.test.media.AbstractTest;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TheSimpsonsCrudIT extends AbstractTest {
  private static final Logger LOGGER = Logger.getLogger(TheSimpsonsCrudIT.class);

  private Long seriesId;
  
  @Test
  @Order(1)
  void testCreateSeries() throws IOException, InterruptedException {
    var createCommand = SeriesCreateCommand.builder()
        .tmdbId(456)
        .name("The Simpsons")
        .score(8.015f)
        .status(ShowStatus.RETURNING_SERIES)
        .type("Scripted")
        .homepage("http://www.thesimpsons.com/")
        .originalLanguage("en")
        .backdrop("/rCTLaPwuApDx8vLGjYZ9pRl7zRB.jpg")
        .poster("/n8V61f1v7idya4WJzGEJNoIp9iL.jpg")
        .overview("Set in Springfield, the average American town, the show focuses on the antics and everyday adventures of the Simpson family; Homer, Marge, Bart, Lisa and Maggie, as well as a virtual cast of thousands. Since the beginning, the series has been a pop culture icon, attracting hundreds of celebrities to guest star. The show has also made name for itself in its fearless satirical take on politics, media and American life in general.")                 
        .build();
    var request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(SERIES_URL).build())
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(createCommand))).build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    var series = OBJECT_MAPPER.readValue(response.body(), SeriesDto.class);
    seriesId = series.id();
    assertEquals(201, response.statusCode());
    LOGGER.infof("Created series in: %d ms", et);
  }
  
  @Test
  @Order(2)
  void testFindById() throws IOException, InterruptedException {
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(SERIES_URL).path(seriesId.toString()).build())
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    var series = OBJECT_MAPPER.readValue(response.body(), SeriesDto.class);
    assertEquals(200, response.statusCode());
    assertEquals(seriesId, series.id());
    assertEquals(456, series.tmdbId());
    assertEquals("The Simpsons", series.name());
    assertEquals(8.015f, series.score());
    assertEquals(ShowStatus.RETURNING_SERIES, series.status());
    assertEquals("Scripted", series.type());
    assertEquals("http://www.thesimpsons.com/", series.homepage());
    assertEquals("/rCTLaPwuApDx8vLGjYZ9pRl7zRB.jpg", series.backdrop());
    assertEquals("/n8V61f1v7idya4WJzGEJNoIp9iL.jpg", series.poster());
    assertEquals("Set in Springfield, the average American town, the show focuses on the antics and everyday adventures of the Simpson family; Homer, Marge, Bart, Lisa and Maggie, as well as a virtual cast of thousands. Since the beginning, the series has been a pop culture icon, attracting hundreds of celebrities to guest star. The show has also made name for itself in its fearless satirical take on politics, media and American life in general.", series.overview());    
    LOGGER.infof("Found default series details in: %d ms", et);
  }
  
  @Test
  @Order(3)
  void testFindByTmdbId() throws IOException, InterruptedException {
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(SERIES_URL).path("tmdb/456").build())
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    var series = OBJECT_MAPPER.readValue(response.body(), SeriesDto.class);
    assertEquals(200, response.statusCode());
    assertEquals(seriesId, series.id());
    assertEquals(456, series.tmdbId());
    assertEquals("The Simpsons", series.name());
    assertEquals(8.015f, series.score());
    assertEquals(ShowStatus.RETURNING_SERIES, series.status());
    assertEquals("Scripted", series.type());
    assertEquals("http://www.thesimpsons.com/", series.homepage());
    assertEquals("/rCTLaPwuApDx8vLGjYZ9pRl7zRB.jpg", series.backdrop());
    assertEquals("/n8V61f1v7idya4WJzGEJNoIp9iL.jpg", series.poster());
    assertEquals("Set in Springfield, the average American town, the show focuses on the antics and everyday adventures of the Simpson family; Homer, Marge, Bart, Lisa and Maggie, as well as a virtual cast of thousands. Since the beginning, the series has been a pop culture icon, attracting hundreds of celebrities to guest star. The show has also made name for itself in its fearless satirical take on politics, media and American life in general.", series.overview());    
    LOGGER.infof("Found default series details by TMDB id in: %d ms", et);
  }
  
  @Test
  @Order(4)
  void testUpdateMovie() throws IOException, InterruptedException {
    var patchRequest = SeriesUpdateCommand.builder()
        .name("X")
        .status(ShowStatus.CANCELED)
        .build();
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(SERIES_URL).path(seriesId.toString()).build())
        .method("PATCH", HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(patchRequest)))
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    var series = OBJECT_MAPPER.readValue(response.body(), SeriesDto.class);
    assertEquals(200, response.statusCode());
    assertEquals(seriesId, series.id());
    assertEquals(456, series.tmdbId());
    assertEquals("X", series.name());
    assertEquals(8.015f, series.score());
    assertEquals(ShowStatus.CANCELED, series.status());
    assertEquals("Scripted", series.type());
    assertEquals("http://www.thesimpsons.com/", series.homepage());
    assertEquals("/rCTLaPwuApDx8vLGjYZ9pRl7zRB.jpg", series.backdrop());
    assertEquals("/n8V61f1v7idya4WJzGEJNoIp9iL.jpg", series.poster());
    assertEquals("Set in Springfield, the average American town, the show focuses on the antics and everyday adventures of the Simpson family; Homer, Marge, Bart, Lisa and Maggie, as well as a virtual cast of thousands. Since the beginning, the series has been a pop culture icon, attracting hundreds of celebrities to guest star. The show has also made name for itself in its fearless satirical take on politics, media and American life in general.", series.overview());    
    LOGGER.infof("Updated movie in: %d ms", et);
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
    LOGGER.infof("Deleted series in: %d ms", et);
  }

  @Test
  @Order(6)
  void testVerifyDeletedMovie() throws IOException, InterruptedException {
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(SERIES_URL).path(seriesId.toString()).build())
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(404, response.statusCode());
    LOGGER.infof("Verified deleted series in: %d ms", et);
  }

}
