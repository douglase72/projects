package com.erdouglass.emdb.media.person.adapter.in.graphql;

import java.time.LocalDate;

import org.eclipse.microprofile.graphql.NonNull;

import com.erdouglass.emdb.media.person.domain.Gender;

public record PersonResponse(
    @NonNull String id,
    @NonNull Long version,
    @NonNull String name,
    LocalDate birthDate,
    LocalDate deathDate,
    @NonNull Gender gender,
    String biography) {}
