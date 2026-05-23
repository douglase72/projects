package com.erdouglass.emdb.media.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SavePerson(
    @NotNull @Positive Integer tmdbId,
    @NotBlank String name) implements SaveCommand {

}
