package com.erdouglass.emdb.common.api.query;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MovieDto(
    @NotNull @Positive Long id,
    @NotBlank String title,
    LocalDate releaseDate) {

}
