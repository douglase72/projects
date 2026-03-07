package com.erdouglass.emdb.test.media;

import java.net.http.HttpClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public abstract class AbstractTest {
  protected static final HttpClient HTTP_CLIENT;
  protected static final ObjectMapper OBJECT_MAPPER;
  protected static final String MOVIES_URL;
  protected static final String PEOPLE_URL;
  protected static final String SERIES_URL;
  protected static final String ROLES_URL;
    
  static {
    HTTP_CLIENT = HttpClient.newBuilder().build();
    OBJECT_MAPPER = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .registerModule(new Jdk8Module());
    MOVIES_URL = "http://localhost:60316/emdb-media/api/movies";
    PEOPLE_URL = "http://localhost:60316/emdb-media/api/people";
    SERIES_URL = "http://localhost:60316/emdb-media/api/series";
    ROLES_URL  = "http://localhost:60316/emdb-media/api/roles";
  }
  
}
