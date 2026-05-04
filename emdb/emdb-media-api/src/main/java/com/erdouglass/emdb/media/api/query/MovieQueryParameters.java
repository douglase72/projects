package com.erdouglass.emdb.media.api.query;

import jakarta.validation.constraints.Positive;

/// Pagination and sorting parameters for movie list queries.
///
/// Shared between the gateway and media service layers. The gateway
/// binds these from HTTP query parameters via a JAX-RS-annotated
/// subclass; the media service receives them after gRPC transport.
/// All fields are optional — the service applies defaults when values
/// are absent.
///
/// @param page the 1-based page number
/// @param size the number of results per page
/// @param sort the field to sort by (e.g. `score`, `title`, `releaseDate`)
public record MovieQueryParameters(
    @Positive Integer page,
    @Positive Integer size,
    String sort) {}
