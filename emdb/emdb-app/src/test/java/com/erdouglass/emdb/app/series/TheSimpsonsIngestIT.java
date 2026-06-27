package com.erdouglass.emdb.app.series;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.Instant;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import com.erdouglass.emdb.app.TestHelper;
import com.erdouglass.emdb.media.IngestMedia;
import com.erdouglass.emdb.media.IngestMedia.Source;
import com.erdouglass.emdb.media.IngestMedia.Type;

class TheSimpsonsIngestIT {
  private static final Logger LOGGER = Logger.getLogger(TheSimpsonsIngestIT.class);
  
  @Test
  void testSeriesIngest() throws IOException, InterruptedException {
    var command = IngestMedia.of(456, Type.SERIES, Source.CLI);
    var request = HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(TestHelper.OBJECT_MAPPER.writeValueAsString(command)))
        .uri(UriBuilder.fromUri(TestHelper.INGEST_URL).build())
        .build();
    var start = Instant.now();
    var response = TestHelper.HTTP_CLIENT.send(request, BodyHandlers.ofString());
    var et = Duration.between(start, Instant.now()).toMillis();
    var jobId = response.body();
    assertEquals(202, response.statusCode());
    LOGGER.infof("Series ingest request %s completed in %d ms", jobId, et);    
  }
}
