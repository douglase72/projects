package com.erdouglass.emdb.test.scraper.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import com.erdouglass.emdb.test.scraper.AbstractTest;

class MovieCronTest extends AbstractTest {
  private static final Logger LOGGER = Logger.getLogger(MovieCronTest.class);
  
  @Test
  void testMovieCron() throws IOException, InterruptedException {
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(MOVIES_URL).path("cron").build())
        .POST(HttpRequest.BodyPublishers.noBody())
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(202, response.statusCode());
    LOGGER.infof("Movie cron took: %d ms", et);
  }

}
