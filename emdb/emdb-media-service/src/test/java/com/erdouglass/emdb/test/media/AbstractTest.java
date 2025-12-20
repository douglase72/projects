package com.erdouglass.emdb.test.media;

import java.io.IOException;
import java.net.http.HttpClient;

import org.eclipse.microprofile.config.ConfigProvider;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public abstract class AbstractTest {
  protected static final HttpClient HTTP_CLIENT;
  protected static final ObjectMapper OBJECT_MAPPER;
  protected static final String MOVIES_URL;
  private static final String DB_PASSWORD;
  private static final String DB_USERNAME;
    
  static {
    HTTP_CLIENT = HttpClient.newBuilder().build();
    OBJECT_MAPPER = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .registerModule(new Jdk8Module());
    MOVIES_URL = "http://localhost:60318/emdb-media/api/movies";
    DB_PASSWORD = ConfigProvider.getConfig().getValue("quarkus.datasource.password", String.class);
    DB_USERNAME = ConfigProvider.getConfig().getValue("quarkus.datasource.username", String.class);
  }
  
  @BeforeAll
  static void initDatabase() throws IOException {
    Flyway flyway = Flyway.configure()
        .dataSource("jdbc:postgresql://localhost:5432/dev", DB_USERNAME, DB_PASSWORD)
        .schemas("emdb_media")
        .cleanDisabled(false)
        .load();
    flyway.clean();
    flyway.migrate();
  }
  
}
