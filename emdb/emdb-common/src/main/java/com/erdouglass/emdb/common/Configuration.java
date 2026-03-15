package com.erdouglass.emdb.common;

import jakarta.enterprise.context.ApplicationScoped;

/// Global configuration constants for the EMDB application.
///
/// Provides standardized string literals, keys, and boundary lengths used
/// across various components, including routing and job scheduling.
@ApplicationScoped
public class Configuration {
/// The standard length for ISO 639-1 language codes.
  public static final int ISO_639_1_LENGTH = 2;
  
/// The maximum allowed length for a URL string.
  public static final int URL_MAX_LENGTH = 2048;
  
  public static final String ADMIN = "admin";
  
  /// Query parameter key used to append additional data to API responses.
  public static final String APPEND = "append";
    
  public static final int GATEWAY_READ_TIMEOUT = 2;
  
  public static final int GATEWAY_WRITE_TIMEOUT = 6;
  
  public static final int TMDB_READ_TIMEOUT = 5;
  
  /// Key used for routing or tagging media ingest events.
  public static final String INGEST_KEY = "ingest.media";
  
  /// Key representing the start time of a background job.
  public static final String JOB_START_TIME = "job-start-time";
  
  public static final String SPACE = " ";
  
  public static final String UNDERSCORE = "_";
  
  /// Key representing the original state or resolution of a media asset.
  public static final String ORIGINAL = "original";
  
  public static final String USER = "user";
}
