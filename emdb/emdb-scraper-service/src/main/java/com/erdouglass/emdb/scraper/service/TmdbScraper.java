package com.erdouglass.emdb.scraper.service;

import jakarta.inject.Inject;
import jakarta.validation.Validator;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.common.message.IngestMessage;
import com.erdouglass.emdb.scraper.client.TmdbPersonClient;
import com.erdouglass.emdb.scraper.mapper.TmdbPersonMapper;

public abstract class TmdbScraper {
  
  @Inject
  @ConfigProperty(name = "tmdb.cast.limit")
  Integer castLimit;

  @Inject
  @ConfigProperty(name = "tmdb.crew.limit")
  Integer crewLimit;
  
  @Inject
  @ConfigProperty(name = "tmdb.rate.limit")
  Integer rateLimit;
  
  @Inject
  @RestClient
  TmdbPersonClient personClient;
  
  @Inject
  TmdbPersonMapper personMapper;
  
  @Inject
  Validator validator;
  
  public abstract void onMessage(IngestMessage message);

}
