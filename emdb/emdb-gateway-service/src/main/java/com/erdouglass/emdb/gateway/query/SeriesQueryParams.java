package com.erdouglass.emdb.gateway.query;

import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.QueryParam;

public record SeriesQueryParams(
    @Positive @QueryParam("page") Integer page,
    @Positive @QueryParam("size") Integer size,
    @QueryParam("sort") String sort) {

}
