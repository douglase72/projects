package com.erdouglass.emdb.media.movie.adapter.in.rest;

import java.math.BigDecimal;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import com.erdouglass.emdb.shared.kernel.CorrelationId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record SaveMovieRequest(
    @NotNull CorrelationId correlationId,
    @NotBlank String title,
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}") String releaseDate,
    @Min(0) @Max(10) BigDecimal score,
    @Pattern(regexp = "[a-z]{2}") String originalLanguage,
    String overview) {
  
}
