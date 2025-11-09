package com.erdouglass.hello.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.TimeUnit;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.UriBuilder;

class HelloTest {
	private static final Logger LOGGER = Logger.getLogger(HelloTest.class);
  private static final HttpClient HTTP_CLIENT;
  private static final String HELLO_URL;
  
  static {
    HTTP_CLIENT = HttpClient.newBuilder().build();
    HELLO_URL = "http://localhost:60300/hello";
  }
  
  @Test
  void testGreeting() throws IOException, InterruptedException {
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(HELLO_URL).build())
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(200, response.statusCode()); 
    assertEquals("Hello dev profile", response.body());
    LOGGER.infof("Greeting test took: %d ms", et);
  }

}
