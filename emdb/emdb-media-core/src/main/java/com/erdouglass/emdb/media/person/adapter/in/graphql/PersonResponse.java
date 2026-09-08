package com.erdouglass.emdb.media.person.adapter.in.graphql;

import java.time.LocalDate;
import java.util.UUID;

import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Type;

import com.erdouglass.emdb.media.person.domain.model.Gender;

@Type("PersonResponse")
public record PersonResponse(    
    @NonNull UUID id,
    @NonNull Long version,
    @NonNull String name,
    LocalDate birthDate,
    LocalDate deathDate,
    Gender gender,
    String biography) { }
