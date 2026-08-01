package com.erdouglass.emdb.ingest.adapter.outbound.tmdb;

import java.util.Objects;

public record TmdbMovieResponse(    
    Integer id,
    String title,
    String release_date,
    String original_language) {

  public TmdbMovieResponse {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(title, "title must not be null");
    Objects.requireNonNull(original_language, "original_language must not be null");
  }
}
