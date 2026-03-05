package com.erdouglass.emdb.test.gateway;

import java.net.http.HttpClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public abstract class AbstractTest {
  protected static final HttpClient HTTP_CLIENT;
  protected static final ObjectMapper OBJECT_MAPPER;
  protected static final String INGEST_URL;
  protected static final String MOVIES_URL;
  protected static final String PEOPLE_URL;
  protected static final String SCHEDULER_URL;
  protected static final String SERIES_URL;
  
  static {
    HTTP_CLIENT = HttpClient.newBuilder().build();
    OBJECT_MAPPER = new ObjectMapper()
        .registerModule(new JavaTimeModule());
    INGEST_URL = "http://localhost:60310/emdb/api/ingest";
    SCHEDULER_URL = "http://localhost:60310/emdb/api/scheduler";
    MOVIES_URL = "http://localhost:60310/emdb/api/movies";
    PEOPLE_URL = "http://localhost:60310/emdb/api/people";
    SERIES_URL = "http://localhost:60310/emdb/api/series";
  }  

}

