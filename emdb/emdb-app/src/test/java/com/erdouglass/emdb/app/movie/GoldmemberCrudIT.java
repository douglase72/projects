package com.erdouglass.emdb.app.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import com.erdouglass.emdb.app.TestHelper;
import com.erdouglass.emdb.media.SaveMovie;
import com.erdouglass.emdb.media.SaveResult;
import com.erdouglass.emdb.media.show.ShowStatus;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GoldmemberCrudIT {
  private static final Logger LOGGER = Logger.getLogger(GoldmemberCrudIT.class);
  
  private Long movieId;

  @Test
  @Order(1)
  void testSaveMovie() throws IOException, InterruptedException {    
    var command = SaveMovie.builder()
        .externalId(818)
        .title("Austin Powers in Goldmember")
        .releaseDate(LocalDate.parse("2002-07-26"))
        .score(BigDecimal.valueOf(5.992))
        .status(ShowStatus.RELEASED)
        .runtime(94)
        .budget(63000000L)
        .revenue(296938801L)
        .backdrop(TestHelper.image("019e5c92-5a24-7517-8b7a-3734166ad76a.jpg"))
        .poster(TestHelper.image("019e5c8d-efdc-7687-b6c7-a6e822fb6d6d.jpg"))        
        .homepage("https://www.warnerbros.com/movies/austin-powers-goldmember")
        .originalLanguage("en")
        .tagline("The grooviest movie of the summer has a secret, baby!")
        .overview("The world's most shagadelic spy continues his fight against Dr. Evil. This time, the diabolical doctor and his clone, Mini-Me, team up with a new foe—'70s kingpin Goldmember. While pursuing the team of villains to stop them from world domination, Austin gets help from his dad and an old girlfriend.")
        .build();
    var request = HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(TestHelper.OBJECT_MAPPER.writeValueAsString(command)))
        .uri(UriBuilder.fromUri(TestHelper.MEDIA_URL).path("movies").build())
        .build();    
    var start = Instant.now();
    var response = TestHelper.HTTP_CLIENT.send(request, BodyHandlers.ofString());
    var et = Duration.between(start, Instant.now()).toMillis();
    assertEquals(200, response.statusCode(), "Server failed with response: " + response.body());    
    var result = TestHelper.OBJECT_MAPPER.readValue(response.body(), SaveResult.class);
    movieId = result.id();
    LOGGER.infof("Saved Austin Powers in Goldmember in %d ms", et);
  }
}
