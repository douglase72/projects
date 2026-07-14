package com.erdouglass.emdb.app;

import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import com.erdouglass.emdb.media.Image;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class TestHelper {
  public static final HttpClient HTTP_CLIENT;
  public static final ObjectMapper OBJECT_MAPPER;
  public static final String GRAPHQL_URL;
  public static final String MOVIES_URL;
  public static final String PEOPLE_URL;
  public static final String SERIES_URL;
  
  static {
    HTTP_CLIENT = HttpClient.newBuilder().build();
    OBJECT_MAPPER = new ObjectMapper()
        .registerModule(new JavaTimeModule());
    MOVIES_URL  = "http://localhost:60310/emdb/api/movies";
    PEOPLE_URL  = "http://localhost:60310/emdb/api/people";
    SERIES_URL  = "http://localhost:60310/emdb/api/series";
    GRAPHQL_URL = "http://localhost:60310/emdb/graphql";
  }  

  private TestHelper() {}
  
  public static Image image(String image) throws IOException {
    var dir = "/home/erdouglass/projects/emdb/emdb-app/test-data";
    byte[] data = Files.readAllBytes(Path.of(dir, image));
    return new Image(UUID.fromString(image.replace(".jpg", "")), data);
  } 
}
