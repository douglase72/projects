package com.erdouglass.emdb.test.gateway.series;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import com.erdouglass.emdb.common.command.IngestRequest;
import com.erdouglass.emdb.test.gateway.AbstractTest;

class SeriesIngestIT extends AbstractTest {
  private static final Logger LOGGER = Logger.getLogger(SeriesIngestIT.class);
  
  @Test
  void testIngest() throws IOException, InterruptedException {
    var ingestRequest = new IngestRequest(456);
    var request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(SERIES_URL).path("ingest").build())
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(ingestRequest))).build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(202, response.statusCode());
    LOGGER.infof("Ingest series request took: %d ms", et);
  }

}
