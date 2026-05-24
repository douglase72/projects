package com.erdouglass.emdb.media.api.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SaveSeries(
    @NotNull @Positive Integer tmdbId,
    @NotBlank String title) implements SaveCommand {

}
