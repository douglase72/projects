package com.erdouglass.emdb.media.internal;

import java.time.LocalDate;

import com.erdouglass.emdb.media.Gender;
import com.erdouglass.emdb.media.Image;

public record PersonData(
    Integer tmdbId,
    String name,
    LocalDate birthDate,
    LocalDate deathDate,
    Gender gender,
    Image profile,
    String homepage,
    String birthPlace,
    String biography) {}
