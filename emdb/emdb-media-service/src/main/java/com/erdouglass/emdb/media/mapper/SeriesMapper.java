package com.erdouglass.emdb.media.mapper;

import jakarta.enterprise.context.ApplicationScoped;

import com.erdouglass.emdb.common.comand.SaveSeries;
import com.erdouglass.emdb.common.comand.UpdateSeries;
import com.erdouglass.emdb.common.query.SeriesDto;
import com.erdouglass.emdb.media.entity.Series;

@ApplicationScoped
public class SeriesMapper {
  
  public Series toSeries(SaveSeries command) {
    var series = new Series(command.tmdbId(), command.title());
    series.score(command.score());
    series.status(command.status());
    series.type(command.type());
    series.homepage(command.homepage());
    series.originalLanguage(command.originalLanguage());
    series.backdrop(command.backdrop());
    series.tmdbBackdrop(command.tmdbBackdrop());
    series.poster(command.poster());
    series.tmdbPoster(command.tmdbPoster());
    series.tagline(command.tagline());
    series.overview(command.overview());
    return series;
  }
  
  public Series toSeries(UpdateSeries command) {
    var series = new Series(command.title());
    series.score(command.score());
    series.status(command.status());
    series.type(command.type());
    series.homepage(command.homepage());
    series.originalLanguage(command.originalLanguage());
    series.backdrop(command.backdrop());
    series.poster(command.poster());
    series.tagline(command.tagline());
    series.overview(command.overview());
    return series;    
  }
  
  public SaveSeries toSaveSeries(Series series) {
    return SaveSeries.builder()
        .tmdbId(series.tmdbId())
        .title(series.title())
        .score(series.score().orElse(null))
        .status(series.status().orElse(null))
        .homepage(series.homepage().orElse(null))
        .originalLanguage(series.originalLanguage().orElse(null))
        .backdrop(series.backdrop().orElse(null))
        .tmdbBackdrop(series.tmdbBackdrop().orElse(null))
        .poster(series.poster().orElse(null))
        .tmdbPoster(series.tmdbPoster().orElse(null))
        .tagline(series.tagline().orElse(null))
        .overview(series.overview().orElse(null))       
        .build();
  }
  
  public SeriesDto toSeriesDto(Series series) {
    return SeriesDto.builder()
        .id(series.id())
        .tmdbId(series.tmdbId())
        .title(series.title())
        .firstAirDate(series.firstAirDate().orElse(null))
        .score(series.score().orElse(null))
        .status(series.status().orElse(null))
        .type(series.type().orElse(null))
        .homepage(series.homepage().orElse(null))
        .originalLanguage(series.originalLanguage().orElse(null))
        .backdrop(series.backdrop().map(p -> String.format("%s.jpg", p)).orElse(null))
        .poster(series.poster().map(p -> String.format("%s.jpg", p)).orElse(null))
        .tagline(series.tagline().orElse(null))
        .overview(series.overview().orElse(null))
        .build();
  }

}
