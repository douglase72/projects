package com.erdouglass.emdb.media.movie.adapter.in.rest;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public record SaveMovieRequest(    
    String title,
    String releaseDate,
    BigDecimal score,
    String originalLanguage,
    String overview) { }
