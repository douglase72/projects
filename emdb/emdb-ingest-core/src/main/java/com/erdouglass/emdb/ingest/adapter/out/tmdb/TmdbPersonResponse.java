package com.erdouglass.emdb.ingest.adapter.out.tmdb;

import java.util.Objects;

public record TmdbPersonResponse(
    Integer id,
    String name,
    String birthday,
    String deathday,
    Integer gender,
    String biography) {

  public TmdbPersonResponse {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(name, "name must not be null");
    Objects.requireNonNull(gender, "gender must not be null");
  }
}
