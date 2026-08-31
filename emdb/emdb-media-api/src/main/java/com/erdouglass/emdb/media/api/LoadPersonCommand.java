package com.erdouglass.emdb.media.api;

import com.erdouglass.common.util.DateTime;

import lombok.Builder;

@Builder
public record LoadPersonCommand(
    Integer tmdbId,
    String name,
    DateTime birthDate,
    DateTime deathDate,
    String gender,
    String biography) {

}
