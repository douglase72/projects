package com.erdouglass.emdb.app.movie;

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
import com.erdouglass.emdb.ingest.adapter.inbound.rest.IngestMediaRequest;
import com.erdouglass.emdb.ingest.domain.model.IngestType;

class BladeRunnerIngestIT {
  private static final Logger LOGGER = Logger.getLogger(BladeRunnerIngestIT.class);
  
  @Test
  void testIngestMovie() throws IOException, InterruptedException {
    var ingestRequest = IngestMediaRequest.of(78, IngestType.MOVIE);
    var request = HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(TestHelper.OBJECT_MAPPER.writeValueAsString(ingestRequest)))
        .uri(UriBuilder.fromUri(TestHelper.INGEST_URL).build())
        .build();
    var start = Instant.now();
    var response = TestHelper.HTTP_CLIENT.send(request, BodyHandlers.ofString());
    var et = Duration.between(start, Instant.now()).toMillis();
    var jobId = response.body();
    assertEquals(202, response.statusCode());
    LOGGER.infof("Submitted movie ingest request %s in %d ms", jobId, et);    
  }
}
