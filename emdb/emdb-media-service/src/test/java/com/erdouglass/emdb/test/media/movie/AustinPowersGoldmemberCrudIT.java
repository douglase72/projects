package com.erdouglass.emdb.test.media.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.TimeUnit;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import com.erdouglass.emdb.test.media.AbstractTest;

import jakarta.ws.rs.core.UriBuilder;

class AustinPowersGoldmemberCrudIT extends AbstractTest {
	private static final Logger LOGGER = Logger.getLogger(AustinPowersGoldmemberCrudIT.class);

  @Test
  void testFindById() throws IOException, InterruptedException {
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(MOVIES_URL).path("1").build())
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(200, response.statusCode()); 
    assertEquals("Austin Powers in Goldmember", response.body());
    LOGGER.infof("Found full movie details in: %d ms", et);
  }

}
