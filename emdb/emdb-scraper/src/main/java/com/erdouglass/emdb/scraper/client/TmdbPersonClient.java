package com.erdouglass.emdb.scraper.client;

import java.time.temporal.ChronoUnit;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.scraper.query.TmdbPerson;

@RegisterRestClient(configKey = "tmdb-person")
@ClientHeaderParam(name = "Authorization", value = "Bearer ${tmdb.token}")
public interface TmdbPersonClient {

  @GET
  @Path("/person/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Timeout(value = Configuration.TMDB_TIMEOUT, unit = ChronoUnit.SECONDS)
  @Retry(
      maxRetries = 3, delay = 200, delayUnit = ChronoUnit.MILLIS, jitter = 50,
      abortOn = { NotFoundException.class, BadRequestException.class, NotAuthorizedException.class })
  @CircuitBreaker(requestVolumeThreshold = 10, failureRatio = 0.4, delay = 15, delayUnit = ChronoUnit.SECONDS)  
  public TmdbPerson findById(@PathParam("id") Integer id);
}
