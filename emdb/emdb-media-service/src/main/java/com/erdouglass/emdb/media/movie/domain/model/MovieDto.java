package com.erdouglass.emdb.media.movie.domain.model;

import java.util.List;
import java.util.Objects;

public record MovieDto(MovieDetails details, List<MovieCredit> credits) {

  public MovieDto {
    Objects.requireNonNull(details, "details are required");
    Objects.requireNonNull(credits, "credits are required");
  }
  
  public static MovieDto of(MovieDetails details, List<MovieCredit> credits) {
    return new MovieDto(details, credits);
  }
}
