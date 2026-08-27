package com.erdouglass.emdb.media.movie.application.port.in;

import java.math.BigDecimal;
import java.util.List;

import com.erdouglass.emdb.media.kernel.SourceId;
import com.erdouglass.emdb.media.movie.domain.command.SaveMovieCommand;

import lombok.Builder;

@Builder
public record SaveMovieCommandRecord(
    SourceId sourceId,
    String title,
    String releaseDate,
    BigDecimal score,
    String originalLanguage,
    String overview,
    List<CastMember> cast,
    List<CrewMember> crew) implements SaveMovieCommand {}
