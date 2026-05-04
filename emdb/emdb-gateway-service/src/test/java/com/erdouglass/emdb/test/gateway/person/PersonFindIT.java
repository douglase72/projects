package com.erdouglass.emdb.test.gateway.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import com.erdouglass.emdb.gateway.query.Slice;
import com.erdouglass.emdb.media.api.command.SavePerson;
import com.erdouglass.emdb.media.api.query.PersonView;
import com.erdouglass.emdb.test.gateway.AbstractTest;
import com.fasterxml.jackson.core.type.TypeReference;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonFindIT extends AbstractTest {
  private static final Logger LOGGER = Logger.getLogger(PersonFindIT.class);
  
  private String token;
  
  @BeforeAll
  void setupSecurity() throws IOException, InterruptedException {
    this.token = getAccessToken();
  }
 
  @Test
  @Order(1)
  void testSaveHarrisonFord() throws IOException, InterruptedException {
    var command = SavePerson.builder()
        .tmdbId(3)
        .name("Harrison Ford")
        .birthDate(LocalDate.parse("1942-07-13"))
        .build();
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(PEOPLE_URL).build())
        .header("Authorization", "Bearer " + token)
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command)))
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(201, response.statusCode(), "Server failed with response: " + response.body());
    LOGGER.infof("Saved Harrison Ford in %d ms", et);    
  }
  
  @Test
  @Order(2)
  void testSaveMikeMyers() throws IOException, InterruptedException {
    var command = SavePerson.builder()
        .tmdbId(12073)
        .name("Mike Myers")
        .birthDate(LocalDate.parse("1963-05-25"))
        .build();
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(PEOPLE_URL).build())
        .header("Authorization", "Bearer " + token)
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command)))
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(201, response.statusCode(), "Server failed with response: " + response.body());
    LOGGER.infof("Saved Mike Myers in %d ms", et);    
  }
  
  @Test
  @Order(3)
  void testSaveElizabethHurley() throws IOException, InterruptedException {
    var command = SavePerson.builder()
        .tmdbId(13918)
        .name("Elizabeth Hurley")
        .birthDate(LocalDate.parse("1965-06-10"))
        .build();
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(PEOPLE_URL).build())
        .header("Authorization", "Bearer " + token)
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command)))
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(201, response.statusCode(), "Server failed with response: " + response.body());
    LOGGER.infof("Saved Elizabeth Hurley in %d ms", et);    
  }
  
  @Test
  @Order(4)
  void testFindAllPeople() throws IOException, InterruptedException {
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(PEOPLE_URL).queryParam("page", 1).queryParam("size", "5").build())
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(200, response.statusCode());
    
    var page = OBJECT_MAPPER.readValue(response.body(), new TypeReference<Slice<PersonView>>() {});
    assertEquals(1, page.page());
    assertEquals(5, page.size());
    assertFalse(page.hasNext());
    
    var people = page.results();
    assertEquals("Elizabeth Hurley", people.get(0).name());
    assertEquals("Harrison Ford", people.get(1).name());
    assertEquals("Mike Myers", people.get(2).name());
    LOGGER.infof("Found all people in: %d ms", et); 
  }
}
