package com.erdouglass.emdb.common.api.command;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;

public record SaveMovie(
    @NotBlank String title,
    LocalDate releaseDate) {}
