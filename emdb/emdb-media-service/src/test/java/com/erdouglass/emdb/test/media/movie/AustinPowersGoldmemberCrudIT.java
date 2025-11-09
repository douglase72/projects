package com.erdouglass.emdb.test.media.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import com.erdouglass.emdb.common.command.MovieCreateCommand;
import com.erdouglass.emdb.common.query.MovieDto;
import com.erdouglass.emdb.test.media.AbstractTest;

import jakarta.ws.rs.core.UriBuilder;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AustinPowersGoldmemberCrudIT extends AbstractTest {
	private static final Logger LOGGER = Logger.getLogger(AustinPowersGoldmemberCrudIT.class);
	
	private Long movieId;

  @Test
  @Order(1)
  void testCreateMovie() throws IOException, InterruptedException {
  	var createRequest = new MovieCreateCommand("Austin Powers in Goldmember", LocalDate.parse("2002-02-26"));
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(MOVIES_URL).build())
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(createRequest)))
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    var movie = OBJECT_MAPPER.readValue(response.body(), MovieDto.class);
    movieId = movie.id();
    assertEquals(201, response.statusCode());
    LOGGER.infof("Created movie in: %d ms", et);
  }
	
  @Test
  @Order(2)
  void testFindById() throws IOException, InterruptedException {
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(MOVIES_URL).path(movieId.toString()).build())
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    var movie = OBJECT_MAPPER.readValue(response.body(), MovieDto.class);
    assertEquals(200, response.statusCode()); 
    assertEquals("Austin Powers in Goldmember", movie.title());
    LOGGER.infof("Found full movie details in: %d ms", et);
  }

}
