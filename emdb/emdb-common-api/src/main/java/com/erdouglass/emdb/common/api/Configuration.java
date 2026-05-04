package com.erdouglass.emdb.common.api;

/// Global configuration constants for the EMDB application.
///
/// Provides standardized string literals, keys, and boundary lengths used
/// across various components.
public final class Configuration {
  public static final int ISO_639_1_LENGTH = 2;
  
  public static final int URL_MAX_LENGTH = 2048;
  
  public static final String ADMIN = "admin";
  
  public static final String APPEND = "append";
  
  public static final int GATEWAY_TIMEOUT = 5;
    
  public static final String SPACE = " ";
  
  public static final int TMDB_TIMEOUT = 5;
  
  public static final String UNDERSCORE = "_";
  
  public static final String USER = "user";
  
  private Configuration() {}
}
