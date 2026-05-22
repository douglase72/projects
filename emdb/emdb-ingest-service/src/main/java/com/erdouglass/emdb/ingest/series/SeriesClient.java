package com.erdouglass.emdb.ingest.series;

import java.time.temporal.ChronoUnit;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.erdouglass.common.rest.GzipReaderInterceptor;
import com.erdouglass.emdb.common.Configuration;

import io.smallrye.faulttolerance.api.ExponentialBackoff;

/// MicroProfile REST client for the TMDB series API. 
@RegisterRestClient(configKey = "tmdb-series")
@RegisterProvider(GzipReaderInterceptor.class)
@ClientHeaderParam(name = "Authorization", value = "Bearer ${tmdb.token}")
interface SeriesClient {

  @GET
  @Path("/tv/{id}")
  @Timeout(value = Configuration.TMDB_TIMEOUT, unit = ChronoUnit.SECONDS)
  @Retry(
      maxRetries = 3, delay = 200, delayUnit = ChronoUnit.MILLIS, jitter = 50,
      abortOn = { NotFoundException.class, BadRequestException.class, NotAuthorizedException.class })
  @ExponentialBackoff(factor = 2, maxDelay = 5, maxDelayUnit = ChronoUnit.SECONDS)
  @CircuitBreaker(requestVolumeThreshold = 10, failureRatio = 0.4, delay = 15, delayUnit = ChronoUnit.SECONDS)  
  Series findById(
      @PathParam("id") Integer id, 
      @QueryParam("append_to_response") String append);
}
