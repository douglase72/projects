package com.erdouglass.emdb.scraper.producer;

import jakarta.inject.Inject;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.scraper.client.TmdbPersonClient;
import com.erdouglass.emdb.scraper.mapper.TmdbPersonMapper;

public abstract class TmdbScraper<T> {
  
  @Inject
  @ConfigProperty(name = "tmdb.cast.limit")
  Integer castLimit;

  @Inject
  @ConfigProperty(name = "tmdb.crew.limit")
  Integer crewLimit;
  
  @Inject
  @RestClient
  TmdbPersonClient personClient;
  
  @Inject
  TmdbPersonMapper personMapper;
  
  @Inject
  Validator validator;
  
  public abstract void ingest(@NotNull @Positive Integer tmdbId);
  
  public abstract void synchronize(@NotNull @Positive Long emdbId, @NotNull @Positive Integer tmdbId);

}
