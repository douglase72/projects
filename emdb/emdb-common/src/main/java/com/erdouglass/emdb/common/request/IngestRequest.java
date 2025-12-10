package com.erdouglass.emdb.common.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/// The external Data Transfer Object (DTO) for the Ingest REST Endpoint.
///
/// This represents the JSON payload received by `POST /<show>/ingest`.
/// It is converted into an internal {@link IngestCommand} message.
///
/// @param tmdbId The ID of the movie on The Movie Database (TMDB).
public record IngestRequest(@NotNull @Positive Integer tmdbId) {

}
