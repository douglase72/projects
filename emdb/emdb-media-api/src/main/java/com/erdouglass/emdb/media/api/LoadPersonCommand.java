package com.erdouglass.emdb.media.api;

import com.erdouglass.common.util.DateTime;

public record LoadPersonCommand(
    TmdbId tmdbId,
    String name,
    DateTime birthDate,
    DateTime deathDate,
    String gender,
    String biography) { }
