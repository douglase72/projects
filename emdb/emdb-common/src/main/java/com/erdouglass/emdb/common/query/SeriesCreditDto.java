package com.erdouglass.emdb.common.query;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record SeriesCreditDto(
    @NotNull UUID creditId,
    @NotNull @PositiveOrZero Integer totalEpisodes,
    @PositiveOrZero Integer order) {

}
