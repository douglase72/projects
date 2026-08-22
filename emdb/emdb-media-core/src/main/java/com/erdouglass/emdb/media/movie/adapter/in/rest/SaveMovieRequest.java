package com.erdouglass.emdb.media.movie.adapter.in.rest;

import java.math.BigDecimal;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.media.kernel.Title;

import lombok.Builder;

/// Request body for the ingestion endpoint.
///
/// `Optional` components are used so that the endpoint can express clearing a
/// field. An omitted field is not preserved — the request describes the whole
/// title, and anything left out is removed.
///
/// @param title the display title, required
/// @param releaseDate the release date in ISO-8601 form, empty to clear
/// @param score the rating from 0 to 10, empty to clear
/// @param originalLanguage the ISO 639-1 code, empty to clear
/// @param overview the synopsis, empty to clear
@Builder
public record SaveMovieRequest(
    @NotBlank @Size(max = Title.MAX_LENGTH) String title,
    String releaseDate,
    @Min(0) @Max(10) BigDecimal score,
    @Pattern(regexp = "[a-z]{2}") String originalLanguage,
    String overview) {}
