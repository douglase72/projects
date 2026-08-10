package com.erdouglass.emdb.media.adapter.inbound.movie;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.eclipse.microprofile.graphql.NonNull;

import com.erdouglass.emdb.media.domain.movie.MoviePublicId;

public record MovieView(
    @NonNull String id,
    @NonNull Long version,
    @NonNull String title,
    LocalDate releaseDate,
    BigDecimal score,
    String originalLanguage) {

  public MovieView(
      Long id, 
      long version, 
      String title, 
      LocalDate releaseDate, 
      BigDecimal score, 
      String originalLanguage) {
    this(MoviePublicId.from(id).value(), version, title, releaseDate, score, originalLanguage);
  }
}
