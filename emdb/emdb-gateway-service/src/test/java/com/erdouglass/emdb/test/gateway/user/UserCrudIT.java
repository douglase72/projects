package com.erdouglass.emdb.test.gateway.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import com.erdouglass.emdb.test.gateway.AbstractTest;
import com.erdouglass.emdb.user.api.Theme;
import com.erdouglass.emdb.user.api.command.UpdateUser;
import com.erdouglass.emdb.user.api.query.UserDetails;

import io.github.cdimascio.dotenv.Dotenv;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserCrudIT extends AbstractTest {
  private static final Logger LOGGER = Logger.getLogger(UserCrudIT.class);
  private static final UUID USER_ID = UUID.fromString("930b84af-1c86-4fdc-8abe-a8ed5ef7d490");
  private static final String ME = "me";
  
  private String token;
  
  @BeforeAll
  void setupSecurity() throws IOException, InterruptedException {
    this.token = getAccessToken();
  }
  
  @Test
  @Order(1)
  void testCreateUser() throws IOException, InterruptedException {
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(USERS_URL).path(ME).build())
        .header("Authorization", "Bearer " + token)
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(200, response.statusCode());
    
    var user = OBJECT_MAPPER.readValue(response.body(), UserDetails.class);
    assertEquals(USER_ID, user.id());
    assertEquals("erdouglass", user.username());
    assertEquals("erik.r.douglass@gmail.com", user.email());
    assertEquals("Erik", user.firstName());
    assertEquals("Douglass", user.lastName());
    assertEquals(Theme.LIGHT, user.theme());
    LOGGER.infof("Created erdouglass in: %d ms", et);    
  }
  
  @Test
  @Order(2)
  void testFindUser() throws IOException, InterruptedException {
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(USERS_URL).path(ME).build())
        .header("Authorization", "Bearer " + token)
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(200, response.statusCode());
    
    var user = OBJECT_MAPPER.readValue(response.body(), UserDetails.class);
    assertEquals(USER_ID, user.id());
    assertEquals("erdouglass", user.username());
    assertEquals("erik.r.douglass@gmail.com", user.email());
    assertEquals("Erik", user.firstName());
    assertEquals("Douglass", user.lastName());
    assertEquals(Theme.LIGHT, user.theme());
    LOGGER.infof("Found erdouglass in: %d ms", et);    
  }
  
  @Test
  @Order(3)
  void testUpdateUser() throws IOException, InterruptedException {
    var command = UpdateUser.builder()
        .theme(Theme.DARK)
        .build();    
    var request = HttpRequest.newBuilder()
        .PUT(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command)))
        .uri(UriBuilder.fromUri(USERS_URL).path(ME).build())
        .header("Authorization", "Bearer " + token)
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(200, response.statusCode());
    
    var user = OBJECT_MAPPER.readValue(response.body(), UserDetails.class);
    assertEquals(USER_ID, user.id());
    assertEquals("erdouglass", user.username());
    assertEquals("erik.r.douglass@gmail.com", user.email());
    assertEquals("Erik", user.firstName());
    assertEquals("Douglass", user.lastName());
    assertEquals(Theme.DARK, user.theme());
    LOGGER.infof("Provisioned erdouglass in: %d ms", et);    
  }
  
  @Disabled
  @Test
  @Order(4)
  void testFindUserUnderLoad() throws IOException, InterruptedException {
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(USERS_URL).path(ME).build())
        .header("Authorization", "Bearer " + token)
        .build();
    for (int i = 0; i < 100; i++) {
      long startTime = System.nanoTime();
      var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
      long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
      assertEquals(200, response.statusCode());
      LOGGER.infof("Found erdouglass in: %d ms", et); 
    }
  }
  
  @Override
  protected String getAccessToken() throws IOException, InterruptedException {
    var dotenv = Dotenv.configure().ignoreIfMissing().load();
    var clientId = dotenv.get("TEST_CLIENT_ID");
    var clientSecret = dotenv.get("TEST_CLIENT_SECRET");
    var username = dotenv.get("TEST_USERNAME");
    var password = dotenv.get("TEST_PASSWORD");
    
    var formData = "grant_type=password"
        + "&client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
        + "&client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8)
        + "&username=" + URLEncoder.encode(username, StandardCharsets.UTF_8)
        + "&password=" + URLEncoder.encode(password, StandardCharsets.UTF_8)
        + "&scope=openid";
    var request = HttpRequest.newBuilder()
        .uri(URI.create(KEYCLOAK_TOKEN_URL))
        .header("Content-Type", "application/x-www-form-urlencoded")
        .POST(HttpRequest.BodyPublishers.ofString(formData))
        .build();
    var response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    if (response.statusCode() != 200) {
      throw new RuntimeException("Failed to get token from Keycloak. Status: " + response.statusCode() + " Body: " + response.body());
    }
    var jsonNode = OBJECT_MAPPER.readTree(response.body());
    return jsonNode.get("access_token").asText();
  } 
}
