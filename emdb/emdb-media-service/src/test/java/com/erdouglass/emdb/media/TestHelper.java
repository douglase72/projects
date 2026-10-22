package com.erdouglass.emdb.media;

import java.net.http.HttpClient;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

public final class TestHelper {
  public static final HttpClient HTTP_CLIENT;
  public static final ObjectMapper OBJECT_MAPPER;
  public static final String GRAPHQL_URL;
  public static final String MOVIES_URL;
  
  static {
    HTTP_CLIENT = HttpClient.newBuilder().build();
    OBJECT_MAPPER = JsonMapper.builder()
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .build();
    MOVIES_URL  = "http://localhost:60312/api/v1/movies";
    GRAPHQL_URL = "http://localhost:60312/api/v1/graphql";
  }  

  private TestHelper() { }
}
