package com.erdouglass.emdb.media.application.port.inbound.person;

import java.time.LocalDate;

import org.eclipse.microprofile.graphql.NonNull;

import com.erdouglass.emdb.media.Gender;

public record PersonView(
    @NonNull Long id,
    @NonNull String name,
    LocalDate birthDate,
    LocalDate deathDate,
    @NonNull Gender gender,
    String profile,
    String homepage,
    String birthPlace,
    String biography) {

}
