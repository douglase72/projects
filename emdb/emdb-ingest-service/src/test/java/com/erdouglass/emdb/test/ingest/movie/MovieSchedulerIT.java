package com.erdouglass.emdb.test.ingest.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.Instant;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import com.erdouglass.emdb.ingest.ExecuteScheduler;
import com.erdouglass.emdb.ingest.MediaType;
import com.erdouglass.emdb.test.ingest.AbstractTest;

class MovieSchedulerIT extends AbstractTest {
  private static final Logger LOGGER = Logger.getLogger(MovieSchedulerIT.class);

  @Test
  void testMovieScheduler() throws IOException, InterruptedException {
    var command = new ExecuteScheduler(MediaType.MOVIE);
    var request = HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command)))
        .uri(UriBuilder.fromUri(SCHEDULER_URL).build())
        .build();
    var start = Instant.now();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    var et = Duration.between(start, Instant.now()).toMillis();
    assertEquals(202, response.statusCode());
    LOGGER.infof("Scheduler request completed in %d ms", et);
  }
}
