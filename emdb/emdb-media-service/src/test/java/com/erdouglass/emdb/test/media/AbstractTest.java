package com.erdouglass.emdb.test.media;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.github.cdimascio.dotenv.Dotenv;

public abstract class AbstractTest {
  protected static final HttpClient HTTP_CLIENT;
  protected static final ObjectMapper OBJECT_MAPPER;
  protected static final String KEYCLOAK_TOKEN_URL;
  protected static final String MOVIES_URL;
  protected static final String PEOPLE_URL;
  protected static final String SERIES_URL;
  protected static final String ROLES_URL;
    
  static {
    HTTP_CLIENT = HttpClient.newBuilder().build();
    OBJECT_MAPPER = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .registerModule(new Jdk8Module());
    KEYCLOAK_TOKEN_URL = "http://localhost:8080/realms/projects/protocol/openid-connect/token";
    MOVIES_URL = "http://localhost:60316/emdb-media/api/movies";
    PEOPLE_URL = "http://localhost:60316/emdb-media/api/people";
    SERIES_URL = "http://localhost:60316/emdb-media/api/series";
    ROLES_URL  = "http://localhost:60316/emdb-media/api/roles";
  }
  
  protected String getAccessToken() throws IOException, InterruptedException {
    var dotenv = Dotenv.configure().ignoreIfMissing().load();
    var clientId = dotenv.get("EMDB_CLIENT_ID");
    var clientSecret = dotenv.get("EMDB_CLIENT_SECRET");
    var formData = String.format(
        "grant_type=client_credentials&client_id=%s&client_secret=%s",
        clientId, clientSecret
    );
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
