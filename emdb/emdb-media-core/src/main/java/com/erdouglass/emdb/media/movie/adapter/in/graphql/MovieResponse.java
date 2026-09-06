package com.erdouglass.emdb.media.movie.adapter.in.graphql;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Type;

@Type("MovieResponse")
public record MovieResponse(
    @NonNull UUID id,
    @NonNull Long version,
    @NonNull String title,
    LocalDate releaseDate,
    BigDecimal score,
    String originalLanguage,
    String overview) { }
