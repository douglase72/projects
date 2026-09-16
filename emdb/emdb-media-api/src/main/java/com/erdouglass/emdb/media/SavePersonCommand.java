package com.erdouglass.emdb.media;

public record SavePersonCommand(
    Integer tmdbId,
    String name,
    String birthDate,
    String deathDate,
    String gender,
    String biography) { }
