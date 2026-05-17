package com.erdouglass.emdb.common.command;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.erdouglass.emdb.common.Image;

public record SaveMovieResponse(
    @NotNull @Positive Long id,
    @NotNull @Valid Image backdrop,
    @NotNull @Valid Image poster) {}
