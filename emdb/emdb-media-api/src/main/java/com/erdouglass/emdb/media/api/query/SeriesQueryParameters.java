package com.erdouglass.emdb.media.api.query;

import jakarta.validation.constraints.Positive;

public record SeriesQueryParameters(
    @Positive Integer page,
    @Positive Integer size,
    String sort) {}
