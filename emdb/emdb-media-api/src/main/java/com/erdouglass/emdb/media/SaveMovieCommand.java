package com.erdouglass.emdb.media;

import java.math.BigDecimal;

/// Decouples the Media service public API from the domain allowing them to 
/// evolve independently.
public record SaveMovieCommand(
    Integer tmdbId,
    String title,
    String releaseDate,
    BigDecimal score,
    String originalLanguage,
    String overview) { }
