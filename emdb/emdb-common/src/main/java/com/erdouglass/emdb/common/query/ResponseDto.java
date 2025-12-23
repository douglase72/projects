package com.erdouglass.emdb.common.query;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ResponseDto(
    @NotNull @Positive Long id,
    @NotNull @Positive Integer tmdbId,
    @NotNull @Min(200) @Max(599) Integer status) {

}
