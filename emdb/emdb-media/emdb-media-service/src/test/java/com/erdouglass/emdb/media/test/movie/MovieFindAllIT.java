package com.erdouglass.emdb.media.test.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import com.erdouglass.emdb.media.query.MovieResponse;
import com.erdouglass.emdb.media.query.OffsetPage;
import com.erdouglass.emdb.media.test.TestHelper;
import com.fasterxml.jackson.core.type.TypeReference;

class MovieFindAllIT {
  private static final Logger LOGGER = Logger.getLogger(MovieFindAllIT.class);
  
  @Test
  void testFindAllMovies() throws IOException, InterruptedException {
    var query = """
        query {
          allMovies(query: { page: 1, size: 5, sort: SCORE_DESC }) {
            results {
              id title releaseDate score poster
            }
            page size totalResults
          }
        }
        """;
    var payload = Map.of("query", query);
    var request = HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(TestHelper.OBJECT_MAPPER.writeValueAsString(payload)))
        .header("Content-Type", "application/json")
        .uri(UriBuilder.fromUri(TestHelper.GRAPHQL_URL).build())
        .build(); 
    var start = Instant.now();
    var response = TestHelper.HTTP_CLIENT.send(request, BodyHandlers.ofString());
    var et = Duration.between(start, Instant.now()).toMillis();
    var root = TestHelper.OBJECT_MAPPER.readTree(response.body());
    assertTrue(root.path("errors").isMissingNode(), "GraphQL errors: " + root.path("errors"));
    
    var page = TestHelper.OBJECT_MAPPER.
        treeToValue(root.path("data").path("movies"), new TypeReference<OffsetPage<MovieResponse>>() {});
    assertEquals(1, page.page());
    assertEquals(3, page.size());
    assertEquals(3, page.totalResults());
    var movies = page.results();
    assertEquals("Austin Powers: International Man of Mystery", movies.get(0).title());
    assertEquals("Austin Powers: The Spy Who Shagged Me", movies.get(1).title());
    assertEquals("Austin Powers in Goldmember", movies.get(2).title());
    LOGGER.infof("Found all movies in %d ms", et);      
  }
}
