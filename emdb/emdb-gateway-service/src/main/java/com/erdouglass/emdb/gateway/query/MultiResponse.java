package com.erdouglass.emdb.gateway.query;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/// Per-entity result returned from batch operations.
///
/// Each entry maps a status code to the saved entity's primary key
/// and TMDB ID so the caller can correlate results with the original
/// request list.
public record MultiResponse(
    @NotNull Integer statusCode,
    @NotNull @Positive Long id,
    @NotNull @Positive Integer tmdbId) {}
