package com.erdouglass.emdb.test.gateway.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.common.query.MovieDto;
import com.erdouglass.emdb.test.gateway.AbstractTest;

class AustinPowersGoldmemberSaveIT extends AbstractTest {
  private static final Logger LOGGER = Logger.getLogger(AustinPowersGoldmemberSaveIT.class);
      
  @Test
  void testSaveMovieCommand() throws IOException, InterruptedException {
    var command = SaveMovie.builder()
        .tmdbId(818)
        .title("Austin Powers in Goldmember")
        .releaseDate(LocalDate.parse("2002-07-26"))
        .build(); 
    var request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(MOVIES_URL).build())
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command))).build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    var movie = OBJECT_MAPPER.readValue(response.body(), MovieDto.class);
    assertEquals(200, response.statusCode());
    assertEquals(818, movie.tmdbId());
    assertEquals("Austin Powers in Goldmember", movie.title());
    assertEquals("2002-07-26", movie.releaseDate().toString());
    LOGGER.infof("Saved movie %d in %d ms", movie.id(), et);    
  }

}
