package com.erdouglass.emdb.test.gateway.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

import com.erdouglass.emdb.common.api.MediaType;
import com.erdouglass.emdb.common.api.command.IngestMedia;
import com.erdouglass.emdb.common.api.command.IngestMedia.IngestSource;
import com.erdouglass.emdb.test.gateway.AbstractTest;

@TestInstance(Lifecycle.PER_CLASS)
class MovieIngestIT extends AbstractTest {
  private static final Logger LOGGER = Logger.getLogger(MovieIngestIT.class);
  
  private String token;
  
  @BeforeAll
  void setupSecurity() throws IOException, InterruptedException {
    this.token = getAccessToken();
  }
  
  @Test
  void testIngestAustinPowersInternationalManOfMystery() throws IOException, InterruptedException {
    var command = IngestMedia.of(816, MediaType.MOVIE, IngestSource.CLI);
    var request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(INGESTS_URL).build())
        .header("Authorization", "Bearer " + token)
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command))).build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(202, response.statusCode());
    var correlationId = OBJECT_MAPPER.readValue(response.body(), String.class);
    LOGGER.infof("Ingest Austin Powers: International Man of Mystery request %s completed in %d ms", correlationId, et);
  }
}
