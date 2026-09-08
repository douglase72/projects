package com.erdouglass.emdb.media.person.adapter.in.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

import lombok.Builder;

@Builder
public record UpdatePersonRequest(
    @NotNull @PositiveOrZero Long version,
    @NotBlank String name,
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}") String birthDate,
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}") String deathDate,
    String gender,
    String biography) { }
