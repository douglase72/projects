package com.erdouglass.emdb.test.scraper.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.erdouglass.emdb.common.message.IngestMessage;
import com.erdouglass.emdb.common.message.MovieCreateMessage;
import com.erdouglass.emdb.scraper.service.TmdbMovieScraper;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;

@QuarkusTest
class MovieIngestIT {
  
  @Inject
  @Any
  InMemoryConnector connector;

  @Inject
  TmdbMovieScraper scraper;
  
  @Inject
  Validator validator;
  
  @BeforeEach
  void clear() {
    connector.sink("movie-create-out").clear();
  }
  
  @Test
  void testCreateMessage() {
    InMemorySink<MovieCreateMessage> queue = connector.sink("movie-create-out");
    
    var jobId = UUID.randomUUID();
    scraper.onMessage(IngestMessage.of(jobId, 818));
    
    var createMessage = queue.received().get(0).getPayload();
    var violations = validator.validate(createMessage);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
    assertEquals(jobId, createMessage.id());
    assertEquals(818, createMessage.tmdbId());
    assertEquals("Austin Powers in Goldmember", createMessage.title());
    assertEquals("2002-07-26", createMessage.releaseDate().toString());
    assertEquals(3, createMessage.credits().size());
    assertEquals(3, createMessage.people().size());
  }

}
