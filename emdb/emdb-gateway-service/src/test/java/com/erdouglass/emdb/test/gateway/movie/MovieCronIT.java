package com.erdouglass.emdb.test.gateway.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import com.erdouglass.emdb.common.request.CronRequest;
import com.erdouglass.emdb.test.gateway.AbstractTest;

class MovieCronIT extends AbstractTest {
  private static final Logger LOGGER = Logger.getLogger(MovieCronIT.class);
  private static final String CRON = "cron";
  
  @Test
  void testCron() throws IOException, InterruptedException {
    var cronRequest = new CronRequest();
    var request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(MOVIES_URL).path(CRON).build())
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(cronRequest)))
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(202, response.statusCode());
    LOGGER.infof("Movie cron request completed in %d ms", et);
  }

}
