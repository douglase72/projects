package com.erdouglass.emdb.scraper.mapper;

import com.erdouglass.emdb.common.command.SeriesCreateCommand;
import com.erdouglass.emdb.scraper.dto.TmdbSeries;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TmdbSeriesMapper {

  public SeriesCreateCommand toSeriesCreateCommand(TmdbSeries tmdbSeries) {
    return SeriesCreateCommand.builder()
        .tmdbId(tmdbSeries.id())
        .name(tmdbSeries.name())
        .score(tmdbSeries.vote_average())
        .status(tmdbSeries.status())
        .homepage(tmdbSeries.homepage())
        .originalLanguage(tmdbSeries.original_language())
        .backdrop(tmdbSeries.backdrop_path())
        .poster(tmdbSeries.poster_path())
        .tagline(tmdbSeries.tagline())
        .overview(tmdbSeries.overview())
        .build();
  }
  
}
