package com.erdouglass.emdb.test.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import com.erdouglass.emdb.common.comand.ExecuteScheduler;
import com.erdouglass.emdb.common.comand.ExecuteScheduler.SchedulerType;

class SchedulerIT extends AbstractTest {
  private static final Logger LOGGER = Logger.getLogger(SchedulerIT.class);

  @Test
  void testMovieCron() throws IOException, InterruptedException {
    var command = new ExecuteScheduler(SchedulerType.MOVIES);
    var request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(SCHEDULER_URL).build())
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command)))
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(202, response.statusCode());
    LOGGER.infof("Movie scheduler request completed in %d ms", et);    
  }
  
}
