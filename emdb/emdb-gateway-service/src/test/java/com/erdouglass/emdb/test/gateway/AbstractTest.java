package com.erdouglass.emdb.test.gateway;

import java.net.http.HttpClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public abstract class AbstractTest {
  protected static final HttpClient HTTP_CLIENT;
  protected static final ObjectMapper OBJECT_MAPPER;
  protected static final String MOVIES_URL;
  protected static final String SERIES_URL;
  
  static {
    HTTP_CLIENT = HttpClient.newBuilder().build();
    OBJECT_MAPPER = new ObjectMapper()
        .registerModule(new JavaTimeModule());
    
    // Docker
    //MOVIES_URL = "http://localhost:60330/emdb/api/movies";
    //SERIES_URL = "http://localhost:60330/emdb/api/series";
    
    // Development
    MOVIES_URL = "http://localhost:60310/emdb/api/movies";
    SERIES_URL = "http://localhost:60310/emdb/api/series";
  }  

}

