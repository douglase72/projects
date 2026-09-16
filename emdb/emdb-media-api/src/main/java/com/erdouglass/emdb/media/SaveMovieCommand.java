package com.erdouglass.emdb.media;

import java.math.BigDecimal;

public record SaveMovieCommand(
    Integer tmdbId,
    String title,
    String releaseDate,
    BigDecimal score,
    String originalLanguage,
    String overview) { }
