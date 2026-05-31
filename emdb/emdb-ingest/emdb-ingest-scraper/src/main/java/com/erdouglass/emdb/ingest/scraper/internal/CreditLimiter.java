package com.erdouglass.emdb.ingest.scraper.internal;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.erdouglass.emdb.media.credit.CastCredit;

@ApplicationScoped
public class CreditLimiter {
  
  @Inject
  @ConfigProperty(name = "tmdb.cast.limit")
  Integer castLimit;
  
  @Inject
  @ConfigProperty(name = "tmdb.crew.limit")
  Integer crewLimit;
  
  public <C extends CastCredit, R, T> T limit(
      final List<C> cast,
      final List<R> crew,
      final BiFunction<List<C>, List<R>, T> factory) {
    var limitedCast = cast.stream()
        .sorted(Comparator.comparingInt(CastCredit::order))
        .limit(castLimit)
        .toList();
    var limitedCrew = crew.stream()
        .limit(crewLimit)
        .toList();
    return factory.apply(limitedCast, limitedCrew);
  }
}
