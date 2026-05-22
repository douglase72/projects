package com.erdouglass.emdb.test.ingest;

import java.net.http.HttpClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public abstract class AbstractTest {
  protected static final HttpClient HTTP_CLIENT;
  protected static final ObjectMapper OBJECT_MAPPER;
  protected static final String INGEST_URL;
  protected static final String SCHEDULER_URL;
  
  static {
    HTTP_CLIENT = HttpClient.newBuilder().build();
    OBJECT_MAPPER = new ObjectMapper()
        .registerModule(new JavaTimeModule());
    INGEST_URL = "http://localhost:60314/emdb-ingest/api/ingest";
    //INGEST_URL = "http://localhost:60334/api/ingest";
    SCHEDULER_URL = "http://localhost:60314/emdb-ingest/api/scheduler";
    //SCHEDULER_URL = "http://localhost:60334/api/scheduler";
  }
}
