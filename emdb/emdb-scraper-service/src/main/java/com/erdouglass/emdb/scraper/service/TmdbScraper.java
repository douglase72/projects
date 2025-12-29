package com.erdouglass.emdb.scraper.service;

import jakarta.inject.Inject;
import jakarta.validation.Validator;

import io.micrometer.core.instrument.MeterRegistry;

public abstract class TmdbScraper { 
  
  @Inject
  MeterRegistry registry;
  
  @Inject
  Validator validator;
  
}
