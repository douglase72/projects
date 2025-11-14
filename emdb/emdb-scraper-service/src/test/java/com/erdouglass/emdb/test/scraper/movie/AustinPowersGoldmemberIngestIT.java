package com.erdouglass.emdb.test.scraper.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.erdouglass.emdb.scraper.dto.IngestRequest;
import com.erdouglass.emdb.test.scraper.AbstractTest;

import jakarta.ws.rs.core.UriBuilder;

class AustinPowersGoldmemberIngestIT extends AbstractTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(AustinPowersGoldmemberIngestIT.class);

  @Test
  void testIngest() throws IOException, InterruptedException {
    var ingestRequest = new IngestRequest(818);
    var request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(MOVIES_URL).path("ingest").build())
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(ingestRequest))).build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(202, response.statusCode());
    LOGGER.info("Ingest movie request took: {} ms", et);
  }

}
