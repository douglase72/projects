package com.erdouglass.emdb.test.gateway.series;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
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

import com.erdouglass.emdb.common.comand.SaveSeries;
import com.erdouglass.emdb.common.query.SeriesView;
import com.erdouglass.emdb.gateway.query.Page;
import com.erdouglass.emdb.test.gateway.AbstractTest;
import com.fasterxml.jackson.core.type.TypeReference;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SeriesFindIT extends AbstractTest {
  private static final Logger LOGGER = Logger.getLogger(SeriesFindIT.class);
  
  private String token;
  
  @BeforeAll
  void setupSecurity() throws IOException, InterruptedException {
    this.token = getAccessToken();
  }
  
  @Test
  @Order(1)
  void testSaveTheSimpsons() throws IOException, InterruptedException {
    var command = SaveSeries.builder()
        .tmdbId(456)
        .title("The Simpsons")
        .score(8.015f)
        .build(); 
    var request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(SERIES_URL).build())
        .header("Authorization", "Bearer " + token)
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command))).build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(201, response.statusCode(), "Server failed with response: " + response.body());   
    LOGGER.infof("Saved The Simpsons in %d ms", et);
  }
  
  @Test
  @Order(2)
  void testSave1923() throws IOException, InterruptedException {
    var command = SaveSeries.builder()
        .tmdbId(157744)
        .title("1923")
        .score(8.2f)
        .build(); 
    var request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(SERIES_URL).build())
        .header("Authorization", "Bearer " + token)
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command))).build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(201, response.statusCode(), "Server failed with response: " + response.body());   
    LOGGER.infof("Saved 1923 in %d ms", et);
  }
  
  @Test
  @Order(3)
  void testSaveGameOfThrones() throws IOException, InterruptedException {
    var command = SaveSeries.builder()
        .tmdbId(1399)
        .title("Game of Thrones")
        .score(8.6f)
        .build(); 
    var request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(SERIES_URL).build())
        .header("Authorization", "Bearer " + token)
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command))).build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(201, response.statusCode(), "Server failed with response: " + response.body());   
    LOGGER.infof("Saved Game of Thrones in %d ms", et);
  }
  
  @Test
  @Order(4)
  void testFindAllSeries() throws IOException, InterruptedException {
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(SERIES_URL).queryParam("page", 1).queryParam("size", "5").build())
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(200, response.statusCode());
    
    var page = OBJECT_MAPPER.readValue(response.body(), new TypeReference<Page<SeriesView>>() {});
    assertEquals(1, page.page());
    assertEquals(5, page.size());
    assertFalse(page.hasNext());
    
    var series = page.results();
    assertEquals("Game of Thrones", series.get(0).title());
    assertEquals("1923", series.get(1).title());
    assertEquals("The Simpsons", series.get(2).title());
    LOGGER.infof("Found all series in: %d ms", et); 
  }
}
