package com.erdouglass.emdb.scraper.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.google.common.util.concurrent.RateLimiter;

@ApplicationScoped
public class TmdbRateLimiter {
  private final RateLimiter limiter;
  
  @Inject
  public TmdbRateLimiter(@ConfigProperty(name = "tmdb.rate.limit") double rateLimit) {
    this.limiter = RateLimiter.create(rateLimit);
  }
  
  public void acquire() {
    limiter.acquire();
  }

}
