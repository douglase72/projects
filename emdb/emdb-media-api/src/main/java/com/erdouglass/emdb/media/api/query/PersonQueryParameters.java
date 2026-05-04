package com.erdouglass.emdb.media.api.query;

import jakarta.validation.constraints.Positive;

public record PersonQueryParameters(
    @Positive Integer page,
    @Positive Integer size,
    String sort) {

}
