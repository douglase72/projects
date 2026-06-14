package com.erdouglass.emdb.ingest.scraper;

import java.time.Duration;
import java.time.Instant;

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.command.SaveCommand;
import com.erdouglass.emdb.media.command.SaveMovie;
import com.erdouglass.emdb.media.command.SavePerson;
import com.erdouglass.emdb.media.command.SaveSeries;

@Log
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
class LogInterceptor {
  private static final Logger LOGGER = Logger.getLogger(LogInterceptor.class);
  private static final String MSG = "Ingest of TMDB %s %d extracted from TMDB in %d ms";
  
  @AroundInvoke
  Object scrape(final InvocationContext context) throws Exception {
    var start = Instant.now();
    var result = (SaveCommand) context.proceed();
    var et = Duration.between(start, Instant.now()).toMillis();
    switch (result) {
      case SaveMovie cmd -> LOGGER.infof(MSG, "movie", cmd.tmdbId(), et); 
      case SavePerson cmd -> LOGGER.infof(MSG, "person", cmd.tmdbId(), et);
      case SaveSeries cmd -> LOGGER.infof(MSG, "series", cmd.tmdbId(), et);
    }
    return result;
  }  
}
