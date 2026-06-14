package com.erdouglass.emdb.media.test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;

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
    GRAPHQL_URL = "http://localhost:60316/emdb-media/api/graphql";
    //MOVIES_URL = "http://localhost:60316/emdb-media/api/movies";
    MOVIES_URL = "http://localhost:60336/emdb-media/api/movies";
    PEOPLE_URL = "http://localhost:60316/emdb-media/api/people";
    SERIES_URL = "http://localhost:60316/emdb-media/api/series";
  }  

  private TestHelper() {}
  
  public static Image image(final String tmdbName, final String image) throws IOException {
    var dir = "/home/erdouglass/projects/emdb/emdb-media/emdb-media-service/test-data";
    byte[] data = Files.readAllBytes(Path.of(dir, image));
    return Image.builder().tmdbName(tmdbName).data(data).build();
  } 
}
