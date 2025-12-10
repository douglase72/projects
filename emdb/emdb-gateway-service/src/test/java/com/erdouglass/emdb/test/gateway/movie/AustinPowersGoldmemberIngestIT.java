package com.erdouglass.emdb.test.gateway.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import com.erdouglass.emdb.common.request.IngestRequest;
import com.erdouglass.emdb.test.gateway.AbstractTest;

class AustinPowersGoldmemberIngestIT extends AbstractTest {
  private static final Logger LOGGER = Logger.getLogger(AustinPowersGoldmemberIngestIT.class);
      
  @Test
  void testIngest() throws IOException, InterruptedException {
    var ingestRequest = new IngestRequest(818);
    var request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(MOVIES_URL).path("ingest").build())
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(ingestRequest))).build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    var traceId = response.body();
    assertEquals(202, response.statusCode());
    LOGGER.infof("Movie ingest request id: %s took %d ms", traceId, et);
  }

}
