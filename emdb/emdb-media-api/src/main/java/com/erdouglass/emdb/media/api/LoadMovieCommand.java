package com.erdouglass.emdb.media.api;

import java.math.BigDecimal;

import com.erdouglass.common.util.DateTime;

import lombok.Builder;

/// The contract between the Ingest bounded context and the Media bounded 
/// context.
@Builder
public record LoadMovieCommand(
    Integer tmdbId,
    String title,
    DateTime releaseDate,
    BigDecimal score,
    String originalLanguage,
    String overview) {

}
