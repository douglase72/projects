package com.erdouglass.emdb.media.adapter.inbound.graphql.movie;

import java.time.LocalDate;

import org.eclipse.microprofile.graphql.NonNull;

public record MovieView(
    @NonNull String id,
    @NonNull Long version,
    @NonNull String title,
    LocalDate releaseDate,
    String originalLanguage) {

}
