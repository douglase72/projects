package com.erdouglass.emdb.test.media.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.erdouglass.emdb.media.proto.v1.MovieResponse;
import com.erdouglass.emdb.media.proto.v1.MovieService;
import com.erdouglass.emdb.media.proto.v1.SaveMovieRequest;

import io.quarkus.grpc.GrpcClient;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class GoldmemberCrudIT {
  
  @GrpcClient
  MovieService service;  

  @Test
  void testSaveMovieCommand() {
    SaveMovieRequest command = SaveMovieRequest.newBuilder()
        .setTmdbId(78)
        .setTitle("Blade Runner")
        .setReleaseDate("1982-06-25")
        .build();
    MovieResponse movie = service.save(command).await().indefinitely();
    assertEquals(1L, movie.getId());
    assertEquals(78, movie.getTmdbId());
    assertEquals("Blade Runner", movie.getTitle());
    assertEquals("1982-06-25", movie.getReleaseDate());
  }

}
