package com.erdouglass.emdb.media.mapper;

import jakarta.enterprise.context.ApplicationScoped;

import com.erdouglass.emdb.common.command.SeriesCreateCommand;
import com.erdouglass.emdb.common.query.SeriesDto;
import com.erdouglass.emdb.media.entity.Series;

@ApplicationScoped
public class SeriesMapper {
  
  public Series toSeries(SeriesCreateCommand command) {
    var series = new Series(command.tmdbId(), command.name(), command.status());
    series.score(command.score() == 0 ? null : command.score());
    series.type(command.type());
    series.homepage(command.homepage());
    series.originalLanguage(command.originalLanguage());
    series.backdrop(command.backdrop());
    series.poster(command.poster());
    series.tagline(command.tagline());
    series.overview(command.overview());
    return series;
  }
  
  public SeriesDto toSeriesDto(Series series) {
    return SeriesDto.builder()
        .id(series.id())
        .tmdbId(series.tmdbId())
        .name(series.name())
        .firstAirDate(series.firstAirDate().orElse(null))
        .score(series.score().orElse(null))
        .status(series.status())
        .type(series.type().orElse(null))
        .homepage(series.homepage().orElse(null))
        .originalLanguage(series.originalLanguage().orElse(null))
        .backdrop(series.backdrop().orElse(null))
        .poster(series.poster().orElse(null))
        .tagline(series.tagline().orElse(null))
        .overview(series.overview().orElse(null))
        .build();
  }

}
