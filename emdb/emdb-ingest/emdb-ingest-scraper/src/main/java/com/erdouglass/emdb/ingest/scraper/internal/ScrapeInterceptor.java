package com.erdouglass.emdb.ingest.scraper.internal;

import java.time.Duration;
import java.time.Instant;

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.SaveCommand;
import com.erdouglass.emdb.media.movie.SaveMovie;
import com.erdouglass.emdb.media.person.SavePerson;
import com.erdouglass.emdb.media.series.SaveSeries;

@Scrape
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
class ScrapeInterceptor {
  private static final Logger LOGGER = Logger.getLogger(ScrapeInterceptor.class);
  private static final String MSG = "Ingest of TMDB %d %s extracted from TMDB in %d ms";
  
  @AroundInvoke
  Object scrape(final InvocationContext context) throws Exception {
    var start = Instant.now();
    var result = (SaveCommand) context.proceed();
    var et = Duration.between(start, Instant.now()).toMillis();
    switch (result) {
      case SaveMovie cmd -> LOGGER.infof(MSG, cmd.tmdbId(), "movie", et); 
      case SavePerson cmd -> LOGGER.infof(MSG, cmd.tmdbId(), "person", et);
      case SaveSeries cmd -> LOGGER.infof(MSG, cmd.tmdbId(), "series", et);
    }
    return result;
  }  
}