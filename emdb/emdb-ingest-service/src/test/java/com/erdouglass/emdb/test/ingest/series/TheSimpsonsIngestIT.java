package com.erdouglass.emdb.test.ingest.series;

import static org.junit.jupiter.api.Assertions.*;

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

class TheSimpsonsIngestIT extends AbstractTest {
  private static final Logger LOGGER = Logger.getLogger(TheSimpsonsIngestIT.class);

  @Test
  void testMovieIngest() throws IOException, InterruptedException {
    var command = IngestMedia.of(456, MediaType.SERIES, IngestSource.CLI);
    var request = HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command)))
        .uri(UriBuilder.fromUri(INGEST_URL).build())
        .build();
    var start = Instant.now();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    var et = Duration.between(start, Instant.now()).toMillis();
    var jobId = response.body();
    assertEquals(202, response.statusCode());
    LOGGER.infof("Series ingest request %s completed in %d ms", jobId, et);
  }
}
