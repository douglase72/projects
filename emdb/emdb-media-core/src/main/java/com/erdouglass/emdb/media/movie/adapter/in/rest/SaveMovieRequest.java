package com.erdouglass.emdb.media.movie.adapter.in.rest;

import java.math.BigDecimal;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.Builder;

@Builder
public record SaveMovieRequest(
    @NotBlank String title,
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}") String releaseDate,
    @Min(0) @Max(10) BigDecimal score,
    @Pattern(regexp = "[a-z]{2}") String originalLanguage,
    String overview) {
  
}
