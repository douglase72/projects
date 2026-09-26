package com.erdouglass.emdb.ingest;

import java.net.http.HttpClient;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class TestHelper {
  public static final HttpClient HTTP_CLIENT;
  public static final ObjectMapper OBJECT_MAPPER;
  public static final String INGEST_URL;
  
  static {
    HTTP_CLIENT = HttpClient.newBuilder().build();
    OBJECT_MAPPER = new ObjectMapper();
    INGEST_URL  = "http://localhost:60310/emdb-ingest/api/ingest";
  }  

  private TestHelper() { }
}
