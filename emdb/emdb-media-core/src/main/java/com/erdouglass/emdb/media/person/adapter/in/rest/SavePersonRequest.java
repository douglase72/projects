package com.erdouglass.emdb.media.person.adapter.in.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.media.person.domain.Name;

import lombok.Builder;

@Builder
public record SavePersonRequest(
    @NotBlank @Size(max = Name.MAX_LENGTH) String name,
    String birthDate,
    String deathDate,
    @NotBlank String gender,
    String biography) {}
