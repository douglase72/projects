package com.erdouglass.emdb.test.gateway.movie;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.erdouglass.emdb.common.MediaType;
import com.erdouglass.emdb.common.comand.IngestMedia;
import com.erdouglass.emdb.common.comand.IngestMedia.IngestSource;
import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.gateway.query.OffsetPage;
import com.erdouglass.emdb.test.gateway.AbstractTest;
import com.fasterxml.jackson.core.type.TypeReference;

@TestInstance(Lifecycle.PER_CLASS)
class IngestFindIT extends AbstractTest {
  private static final Logger LOGGER = Logger.getLogger(IngestFindIT.class);
  
  private String token;
  
  @BeforeAll
  void setupSecurity() throws IOException, InterruptedException {
    this.token = getAccessToken();
  }
  
  @Test
  void testFindIngest() throws IOException, InterruptedException {
    var command = IngestMedia.of(818, MediaType.MOVIE, IngestSource.CLI);
    var request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(INGESTS_URL).build())
        .header("Authorization", "Bearer " + token)
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command))).build();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    assertEquals(202, response.statusCode());
    
    command = IngestMedia.of(817, MediaType.MOVIE, IngestSource.CLI);
    request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(INGESTS_URL).build())
        .header("Authorization", "Bearer " + token)
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command))).build();
    response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    assertEquals(202, response.statusCode());
    
    command = IngestMedia.of(816, MediaType.MOVIE, IngestSource.CLI);
    request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(INGESTS_URL).build())
        .header("Authorization", "Bearer " + token)
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command))).build();
    response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    assertEquals(202, response.statusCode());    
    
    TimeUnit.SECONDS.sleep(3);
    
    request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(INGESTS_URL)
            .queryParam("page", "1")
            .queryParam("size", "20")
            .build())
        .header("Authorization", "Bearer " + token)
        .build();
    long startTime = System.nanoTime();
    response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    var page = OBJECT_MAPPER.readValue(response.body(), new TypeReference<OffsetPage<IngestStatusChanged>>() {});
    assertEquals(1, page.page());
    assertEquals(20, page.size());
    assertEquals(3, page.totalResults());
    var events = page.results();
    assertEquals(3, events.size());
    assertEquals(816, events.get(0).tmdbId());
    assertEquals(817, events.get(1).tmdbId());
    assertEquals(818, events.get(2).tmdbId());
    LOGGER.infof("Found all ingest jobs in %d ms", et);
  }
}
