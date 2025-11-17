package com.erdouglass.emdb.test.media.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import com.erdouglass.emdb.common.CreditType;
import com.erdouglass.emdb.common.Gender;
import com.erdouglass.emdb.common.ShowStatus;
import com.erdouglass.emdb.common.command.MovieCreateCommand;
import com.erdouglass.emdb.common.command.MovieCreditCreateCommand;
import com.erdouglass.emdb.common.command.MovieUpdateCommand;
import com.erdouglass.emdb.common.command.PersonCreateCommand;
import com.erdouglass.emdb.common.query.MovieDto;
import com.erdouglass.emdb.test.media.AbstractTest;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AustinPowersGoldmemberCrudIT extends AbstractTest {
  private static final Logger LOGGER = Logger.getLogger(AustinPowersGoldmemberCrudIT.class);

  private Long movieId;

  @Test
  @Order(1)
  void testCreateMovie() throws IOException, InterruptedException {
    var credits = List.of(
        MovieCreditCreateCommand.builder()
          .tmdbId("52fe427bc3a36847f8022183")
          .creditType(CreditType.CAST)
          .personId(12073)
          .role("Austin Powers / Dr. Evil / Goldmember / Fat Bastard")
          .order(0)
          .build(),
        MovieCreditCreateCommand.builder()
          .tmdbId("52fe427bc3a36847f802218b")
          .creditType(CreditType.CAST)
          .personId(13922)
          .role("Scott Evil")
          .order(2)
          .build(),
        MovieCreditCreateCommand.builder()
          .tmdbId("52fe427bc3a36847f8022187")
          .creditType(CreditType.CAST)
          .personId(14386)
          .role("Foxxy Cleopatra")
          .order(1)
          .build());
    
    var people = List.of(
        PersonCreateCommand.builder()
          .tmdbId(14386)
          .name("Beyoncé")
          .gender(Gender.FEMALE)
          .build(),
        PersonCreateCommand.builder()
          .tmdbId(13922)
          .name("Seth Green")
          .gender(Gender.MALE)
          .build(),
        PersonCreateCommand.builder()
          .tmdbId(12073)
          .name("Mike Myers")
          .gender(Gender.MALE)
          .build());
    
    var createCommand = MovieCreateCommand.builder()
        .tmdbId(818)
        .title("Austin Powers in Goldmember")
        .releaseDate(LocalDate.parse("2002-07-26"))
        .score(5.992f)
        .status(ShowStatus.RELEASED)
        .runtime(94)
        .budget(63000000)
        .revenue(296938801)
        .homepage("https://www.warnerbros.com/movies/austin-powers-goldmember")
        .originalLanguage("en")
        .backdrop("/kuPpElzfYnzsCye0hF8EbJSrvwo.jpg")
        .poster("/n8V61f1v7idya4WJzGEJNoIp9iL.jpg")
        .tagline("The grooviest movie of the summer has a secret, baby!")
        .overview("The world's most shagadelic spy continues his fight against Dr. Evil. This time, the diabolical doctor and his clone, Mini-Me, team up with a new foe—'70s kingpin Goldmember. While pursuing the team of villains to stop them from world domination, Austin gets help from his dad and an old girlfriend.")
        .credits(credits)
        .people(people)
        .build();
    var request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(MOVIES_URL).build())
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(createCommand))).build();
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
    assertEquals(818, movie.tmdbId());
    assertEquals("Austin Powers in Goldmember", movie.title());
    assertEquals("2002-07-26", movie.releaseDate().toString());
    assertEquals(5.992f, movie.score());
    assertEquals(ShowStatus.RELEASED, movie.status());
    assertEquals(94, movie.runtime());
    assertEquals(63000000, movie.budget());
    assertEquals(296938801, movie.revenue());
    assertEquals("https://www.warnerbros.com/movies/austin-powers-goldmember", movie.homepage());
    assertEquals("en", movie.originalLanguage());
    assertEquals("/kuPpElzfYnzsCye0hF8EbJSrvwo.jpg", movie.backdrop());
    assertEquals("/n8V61f1v7idya4WJzGEJNoIp9iL.jpg", movie.poster());
    assertEquals("The grooviest movie of the summer has a secret, baby!", movie.tagline());
    assertEquals("The world's most shagadelic spy continues his fight against Dr. Evil. This time, the diabolical doctor and his clone, Mini-Me, team up with a new foe—'70s kingpin Goldmember. While pursuing the team of villains to stop them from world domination, Austin gets help from his dad and an old girlfriend.", movie.overview());
    LOGGER.infof("Found default movie details in: %d ms", et);
  }

  @Test
  @Order(3)
  void testFindByTmdbId() throws IOException, InterruptedException {
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(MOVIES_URL).path("tmdb/818").build())
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    var movie = OBJECT_MAPPER.readValue(response.body(), MovieDto.class);
    assertEquals(200, response.statusCode());
    assertEquals(818, movie.tmdbId());
    assertEquals("Austin Powers in Goldmember", movie.title());
    assertEquals("2002-07-26", movie.releaseDate().toString());
    assertEquals(5.992f, movie.score());
    assertEquals(ShowStatus.RELEASED, movie.status());
    assertEquals(94, movie.runtime());
    assertEquals(63000000, movie.budget());
    assertEquals(296938801, movie.revenue());
    assertEquals("https://www.warnerbros.com/movies/austin-powers-goldmember", movie.homepage());
    assertEquals("en", movie.originalLanguage());
    assertEquals("/kuPpElzfYnzsCye0hF8EbJSrvwo.jpg", movie.backdrop());
    assertEquals("/n8V61f1v7idya4WJzGEJNoIp9iL.jpg", movie.poster());
    assertEquals("The grooviest movie of the summer has a secret, baby!", movie.tagline());
    assertEquals("The world's most shagadelic spy continues his fight against Dr. Evil. This time, the diabolical doctor and his clone, Mini-Me, team up with a new foe—'70s kingpin Goldmember. While pursuing the team of villains to stop them from world domination, Austin gets help from his dad and an old girlfriend.", movie.overview());
    LOGGER.infof("Found default movie details in: %d ms", et);
  }

  @Test
  @Order(4)
  void testUpdateMovie() throws IOException, InterruptedException {
    var patchRequest = MovieUpdateCommand.builder()
        .releaseDate(LocalDate.parse("2000-01-01"))
        .score(6.5f)
        .status(ShowStatus.CANCELED)
        .build();
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(MOVIES_URL).path(movieId.toString()).build())
        .method("PATCH", HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(patchRequest)))
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    var movie = OBJECT_MAPPER.readValue(response.body(), MovieDto.class);
    assertEquals(200, response.statusCode());
    assertEquals(818, movie.tmdbId());
    assertEquals("Austin Powers in Goldmember", movie.title());
    assertEquals("2000-01-01", movie.releaseDate().toString());
    assertEquals(6.5f, movie.score());
    assertEquals(ShowStatus.CANCELED, movie.status());
    assertEquals(94, movie.runtime());
    assertEquals(63000000, movie.budget());
    assertEquals(296938801, movie.revenue());
    assertEquals("https://www.warnerbros.com/movies/austin-powers-goldmember", movie.homepage());
    assertEquals("en", movie.originalLanguage());
    assertEquals("/kuPpElzfYnzsCye0hF8EbJSrvwo.jpg", movie.backdrop());
    assertEquals("/n8V61f1v7idya4WJzGEJNoIp9iL.jpg", movie.poster());
    assertEquals("The grooviest movie of the summer has a secret, baby!", movie.tagline());
    assertEquals(
        "The world's most shagadelic spy continues his fight against Dr. Evil. This time, the diabolical doctor and his clone, Mini-Me, team up with a new foe—'70s kingpin Goldmember. While pursuing the team of villains to stop them from world domination, Austin gets help from his dad and an old girlfriend.",
        movie.overview());
    LOGGER.infof("Updated movie in: %d ms", et);
  }

  @Test
  @Order(5)
  void testDeleteMovie() throws IOException, InterruptedException {
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(MOVIES_URL).path(movieId.toString()).build())
        .DELETE()
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(204, response.statusCode());
    LOGGER.infof("Deleted movie in: %d ms", et);
  }

  @Test
  @Order(6)
  void testVerifyDeletedMovie() throws IOException, InterruptedException {
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(MOVIES_URL).path(movieId.toString()).build())
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(404, response.statusCode());
    LOGGER.infof("Verified deleted movie in: %d ms", et);
  }

}
