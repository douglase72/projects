package com.erdouglass.emdb.test.gateway.series;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import com.erdouglass.emdb.common.MediaType;
import com.erdouglass.emdb.common.comand.IngestMedia;
import com.erdouglass.emdb.common.comand.IngestMedia.IngestSource;
import com.erdouglass.emdb.test.gateway.AbstractTest;

class TheSimpsonsIngestIT extends AbstractTest {
  private static final Logger LOGGER = Logger.getLogger(TheSimpsonsIngestIT.class);
  
  @Test
  void testIngest() throws IOException, InterruptedException {
    var command = IngestMedia.of(456, MediaType.SERIES, IngestSource.CLI);
    var request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(INGEST_URL).build())
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command))).build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    var jobId = response.body();
    assertEquals(202, response.statusCode());
    LOGGER.infof("Series ingest request %s completed in %d ms", jobId, et);
  }

}
