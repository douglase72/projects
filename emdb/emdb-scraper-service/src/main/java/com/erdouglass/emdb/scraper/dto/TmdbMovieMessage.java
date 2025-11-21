package com.erdouglass.emdb.scraper.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TmdbMovieMessage(
    @NotNull @Positive Integer tmdbId,
    @Positive Long emdbId,
    @NotNull MessageType type) {

  public enum MessageType {
    INGEST,
    SYNCHRONIZE;
  }
  
  public static TmdbMovieMessage of(Integer tmdbId, MessageType type) {
    return new TmdbMovieMessage(tmdbId, null, type);
  }
  
  public static TmdbMovieMessage of(Long emdbId, Integer tmdbId, MessageType type) {
    return new TmdbMovieMessage(tmdbId, emdbId, type);
  }
  
}
