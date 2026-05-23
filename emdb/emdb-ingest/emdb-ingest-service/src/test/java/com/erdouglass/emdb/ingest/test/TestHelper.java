package com.erdouglass.emdb.ingest.test;

import java.net.http.HttpClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class TestHelper {
  public static final HttpClient HTTP_CLIENT;
  public static final ObjectMapper OBJECT_MAPPER;
  public static final String INGEST_URL;
  public static final String SCHEDULER_URL;
  
  static {
    HTTP_CLIENT = HttpClient.newBuilder().build();
    OBJECT_MAPPER = new ObjectMapper()
        .registerModule(new JavaTimeModule());
    INGEST_URL = "http://localhost:60314/emdb-ingest/api/ingest";
    //INGEST_URL = "http://localhost:60334/api/ingest";
    SCHEDULER_URL = "http://localhost:60314/emdb-ingest/api/scheduler";
    //SCHEDULER_URL = "http://localhost:60334/api/scheduler";
  }  

  private TestHelper() {}
}