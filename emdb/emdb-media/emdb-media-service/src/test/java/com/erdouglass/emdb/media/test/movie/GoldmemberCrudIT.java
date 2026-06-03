package com.erdouglass.emdb.media.test.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.erdouglass.emdb.media.Image;
import com.erdouglass.emdb.media.movie.MovieResponse;
import com.erdouglass.emdb.media.movie.SaveMovie;
import com.erdouglass.emdb.media.movie.SaveMovie.CastCredit;
import com.erdouglass.emdb.media.movie.SaveMovie.Credits;
import com.erdouglass.emdb.media.movie.SaveMovie.CrewCredit;
import com.erdouglass.emdb.media.person.Gender;
import com.erdouglass.emdb.media.show.ShowStatus;
import com.erdouglass.emdb.media.test.TestHelper;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GoldmemberCrudIT {
  private static final Logger LOGGER = Logger.getLogger(GoldmemberCrudIT.class);
  
  @Test
  @Order(1)
  void testSaveMovie() throws IOException, InterruptedException {
    var credits = new Credits(
        List.of(
            new CastCredit("52fe427bc3a36847f8022183", 12073, "Mike Myers", Gender.MALE,  "/gjfDl52Kk02MPgUYFjs9bOy33OY.jpg", "Austin Powers / Dr. Evil / Goldmember / Fat Bastard", 0),
            new CastCredit("52fe427bc3a36847f802218b", 13922, "Seth Green", Gender.MALE,  "/l4No5Eu6j0U80hCIkaSn17AOWrj.jpg", "Scott Evil", 2),
            new CastCredit("52fe427bc3a36847f8022187", 14386, "Beyoncé", Gender.FEMALE,   "/2HbjNtiCtmbArEnELuDFU7knaVK.jpg", "Foxxy Cleopatra", 1)),
        List.of(
            new CrewCredit("52fe427bc3a36847f8022107", 12073, "Mike Myers", Gender.MALE, "/gjfDl52Kk02MPgUYFjs9bOy33OY.jpg", "Producer"),
            new CrewCredit("52fe427bc3a36847f80220ef", 12073, "Mike Myers", Gender.MALE, "/gjfDl52Kk02MPgUYFjs9bOy33OY.jpg", "Screenplay"),
            new CrewCredit("6758f532ef269d0b88e3939a", 12073, "Mike Myers", Gender.MALE, "/gjfDl52Kk02MPgUYFjs9bOy33OY.jpg", "Characters")));
    
    var command = SaveMovie.builder()
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
        .backdrop(image("/kuPpElzfYnzsCye0hF8EbJSrvwo.jpg", "019e5c92-5a24-7517-8b7a-3734166ad76a.jpg"))
        .poster(image("/n8V61f1v7idya4WJzGEJNoIp9iL.jpg", "019e5c8d-efdc-7687-b6c7-a6e822fb6d6d.jpg"))
        .tagline("The grooviest movie of the summer has a secret, baby!")
        .overview("The world's most shagadelic spy continues his fight against Dr. Evil. This time, the diabolical doctor and his clone, Mini-Me, team up with a new foe—'70s kingpin Goldmember. While pursuing the team of villains to stop them from world domination, Austin gets help from his dad and an old girlfriend.")
        .credits(credits)
        .build();
    var request = HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(TestHelper.OBJECT_MAPPER.writeValueAsString(command)))
        .uri(UriBuilder.fromUri(TestHelper.MOVIES_URL).build())
        .build();    
    var start = Instant.now();
    var response = TestHelper.HTTP_CLIENT.send(request, BodyHandlers.ofString());
    var et = Duration.between(start, Instant.now()).toMillis();
    assertEquals(201, response.statusCode(), "Server failed with response: " + response.body());
    
    var movie = TestHelper.OBJECT_MAPPER.readValue(response.body(), MovieResponse.class);
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
    assertEquals("The grooviest movie of the summer has a secret, baby!", movie.tagline());
    assertEquals("The world's most shagadelic spy continues his fight against Dr. Evil. This time, the diabolical doctor and his clone, Mini-Me, team up with a new foe—'70s kingpin Goldmember. While pursuing the team of villains to stop them from world domination, Austin gets help from his dad and an old girlfriend.", movie.overview());
    LOGGER.infof("Saved Austin Powers in Goldmember in %d ms", et);    
  }
  
  private Image image(String tmdbName, String image) throws IOException {
    var dir = "/home/erdouglass/projects/emdb/emdb-media/emdb-media-service/test-data";
    byte[] data = Files.readAllBytes(Path.of(dir, image));
    return Image.builder().tmdbName(tmdbName).data(data).build();
  }
}
