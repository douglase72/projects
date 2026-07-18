package com.erdouglass.emdb.app;

import java.net.http.HttpClient;

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
}
