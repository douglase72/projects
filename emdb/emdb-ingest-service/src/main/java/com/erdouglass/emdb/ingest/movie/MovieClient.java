package com.erdouglass.emdb.ingest.movie;

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

/// MicroProfile REST client for the TMDB movie API. The base URL is
/// configured under the `tmdb-movie` config key, and every request carries
/// a bearer token sourced from the `tmdb.token` property.
@RegisterRestClient(configKey = "tmdb-movie")
@RegisterProvider(GzipReaderInterceptor.class)
@ClientHeaderParam(name = "Authorization", value = "Bearer ${tmdb.token}")
interface MovieClient {

  /// Fetches a single movie by its TMDB identifier, optionally appending
  /// related sub-resources in the same response.
  ///
  /// ### Fault tolerance
  ///
  /// Each call is wrapped by a layered fault-tolerance policy:
  ///
  /// - **`@Timeout`** — caps an individual attempt at
  ///   [Configuration#TMDB_TIMEOUT] seconds. An attempt that exceeds this
  ///   bound is aborted and treated as a failure by the retry and circuit
  ///   breaker policies.
  ///
  /// - **`@Retry`** — retries failed attempts up to three times. The base
  ///   delay is 200ms with ±50ms of random jitter to prevent a
  ///   *thundering herd* in which many clients retry in lockstep after a
  ///   shared upstream blip. Client-side error responses
  ///   ([NotFoundException], [BadRequestException], [NotAuthorizedException])
  ///   are aborted immediately since retrying them cannot succeed.
  ///
  /// - **`@ExponentialBackoff`** — a SmallRye extension that augments
  ///   `@Retry` by doubling the base delay on each successive attempt
  ///   (200ms → 400ms → 800ms), capped at five seconds. Jitter from
  ///   `@Retry` is still applied on top of each computed delay.
  ///
  /// - **`@CircuitBreaker`** — opens the circuit once at least ten requests
  ///   have been observed in the rolling window and 40% or more have
  ///   failed, short-circuiting subsequent calls for fifteen seconds
  ///   before allowing a trial request through. This protects TMDB from
  ///   sustained load when it is already in trouble and frees this
  ///   service from blocking on calls that are unlikely to succeed.
  ///
  /// @param id the TMDB movie identifier
  /// @param append a comma-separated list of related resources to include
  ///               (e.g., `"credits"`, `"credits,images"`); may be `null`
  /// @return the deserialized [Movie] payload
  @GET
  @Path("/movie/{id}")
  @Timeout(value = Configuration.TMDB_TIMEOUT, unit = ChronoUnit.SECONDS)
  @Retry(
      maxRetries = 3, delay = 200, delayUnit = ChronoUnit.MILLIS, jitter = 50,
      abortOn = { NotFoundException.class, BadRequestException.class, NotAuthorizedException.class })
  @ExponentialBackoff(factor = 2, maxDelay = 5, maxDelayUnit = ChronoUnit.SECONDS)
  @CircuitBreaker(requestVolumeThreshold = 10, failureRatio = 0.4, delay = 15, delayUnit = ChronoUnit.SECONDS)  
  Movie findById(
      @PathParam("id") Integer id, 
      @QueryParam("append_to_response") String append);
}
