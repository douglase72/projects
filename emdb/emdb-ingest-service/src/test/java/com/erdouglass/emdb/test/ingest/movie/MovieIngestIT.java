package com.erdouglass.emdb.test.ingest.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.Instant;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import com.erdouglass.emdb.ingest.IngestMedia;
import com.erdouglass.emdb.ingest.MediaType;
import com.erdouglass.emdb.ingest.IngestMedia.IngestSource;
import com.erdouglass.emdb.test.ingest.AbstractTest;

class MovieIngestIT extends AbstractTest {
  private static final Logger LOGGER = Logger.getLogger(MovieIngestIT.class);

  @Test
  void testIngestAustinPowersInternationalManOfMystery() throws IOException, InterruptedException {
    var command = IngestMedia.of(816, MediaType.MOVIE, IngestSource.CLI);
    var request = HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command)))
        .uri(UriBuilder.fromUri(INGEST_URL).build())
        .build();
    var start = Instant.now();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    var et = Duration.between(start, Instant.now()).toMillis();
    assertEquals(202, response.statusCode());
    LOGGER.infof("Ingest Austin Powers: International Man of Mystery request sent in %d ms", et);
  }
}
