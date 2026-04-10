package com.erdouglass.emdb.gateway.query;

import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.QueryParam;

/// Query parameters for the `GET /movies` endpoint.
///
/// All fields are optional. When omitted, the server applies defaults
/// for page number and page size.
///
/// @param page the 1-based page number
/// @param size the number of results per page
/// @param sort the field to sort by (e.g. `score`, `title`, `releaseDate`)
public record MovieQueryParams(
    @Positive @QueryParam("page") Integer page,
    @Positive @QueryParam("size") Integer size,
    @QueryParam("sort") String sort) {}

