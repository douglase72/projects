package com.erdouglass.emdb.gateway.query;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OffsetPage<T>(
    @NotNull List<T> results,
    @NotNull @Positive Integer page,
    @NotNull @Positive Integer size,
    @NotNull @Positive Long totalResults) {

}
