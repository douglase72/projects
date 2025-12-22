package com.erdouglass.emdb.scraper.service;

import jakarta.inject.Inject;
import jakarta.validation.Validator;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.common.message.IngestMessage;
import com.erdouglass.emdb.scraper.client.TmdbPersonClient;
import com.erdouglass.emdb.scraper.mapper.TmdbPersonMapper;

/// Abstract base class for TMDB ingestion services.
///
/// This class encapsulates shared dependencies and configuration required by specific
/// scraping implementations (e.g., Movies, TV Shows). It provides access to rate limits,
/// external clients for person data, and validation logic to ensure data integrity
/// before processing.
public abstract class TmdbScraper {
  
  /// The maximum number of cast members to ingest per entity.
  /// Defined by the property {@code tmdb.cast.limit}.
  @Inject
  @ConfigProperty(name = "tmdb.cast.limit")
  Integer castLimit;

  /// The maximum number of crew members to ingest per entity.
  /// Defined by the property {@code tmdb.crew.limit}.
  @Inject
  @ConfigProperty(name = "tmdb.crew.limit")
  Integer crewLimit;
  
  /// The maximum number of requests allowed per second for person details.
  /// Defined by the property {@code tmdb.rate.limit}.
  @Inject
  @ConfigProperty(name = "tmdb.rate.limit")
  Integer rateLimit;
  
  /// Client for interacting with the TMDB Person API endpoints.
  @Inject
  @RestClient
  TmdbPersonClient personClient;
  
  /// Mapper for converting TMDB person DTOs into domain messages.
  @Inject
  TmdbPersonMapper personMapper;
  
  /// Validator for ensuring ingested data meets constraints before processing.
  @Inject
  Validator validator;
  
  /// Processes an incoming ingestion message.
  ///
  /// Implementations must define the specific workflow for fetching, enriching,
  /// and publishing data for the specific media type.
  ///
  /// @param message the {@link IngestMessage} containing the ID of the entity to scrape.
  public abstract void onMessage(IngestMessage message);

}
