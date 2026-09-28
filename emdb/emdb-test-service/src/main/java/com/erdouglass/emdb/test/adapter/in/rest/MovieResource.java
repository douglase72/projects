package com.erdouglass.emdb.test.adapter.in.rest;

import java.math.BigDecimal;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.jboss.logging.Logger;

@Path("/api")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class MovieResource {
  private static final Logger LOGGER = Logger.getLogger(MovieResource.class);

  @GET
  @Path("/movie/{id}")
  public TmdbMovieResponse find() {
    var movie = TmdbMovieResponse.builder()
        .id(78)
        .title("Blade Runner")
        .release_date("1982-06-25")
        .vote_average(BigDecimal.valueOf(7.893))
        .vote_count(32)
        .original_language("en")
        .overview("In the smog-choked dystopian Los Angeles of 2019, blade runner Rick Deckard is called out of retirement to terminate a quartet of replicants who have escaped to Earth seeking their creator for a way to extend their short life spans.")
        .build();
    LOGGER.infof("Response: %s", movie);
    return movie;
  }
}
