package com.erdouglass.emdb.media.domain.series;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ObjectFactory;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.media.Image;
import com.erdouglass.emdb.media.series.SaveSeries;
import com.erdouglass.emdb.media.series.SeriesResponse;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
interface SeriesMapper {
  
  @Mapping(target = "firstAirDate", ignore = true)
  @Mapping(source = "command.backdrop.tmdbName", target = "tmdbBackdrop")
  @Mapping(source = "command.backdrop.emdbName", target = "backdrop")
  @Mapping(source = "command.poster.tmdbName",   target = "tmdbPoster")
  @Mapping(source = "command.poster.emdbName",   target = "poster")
  void merge(SaveSeries command, @MappingTarget Series series);

  @Mapping(target = "firstAirDate", ignore = true)
  @Mapping(source = "backdrop.tmdbName", target = "tmdbBackdrop")
  @Mapping(source = "backdrop.emdbName", target = "backdrop")
  @Mapping(source = "poster.tmdbName",   target = "tmdbPoster")
  @Mapping(source = "poster.emdbName",   target = "poster")
  Series toMovie(SaveSeries command, Image backdrop, Image poster);
  
  @Mapping(target = "lastAirDate", ignore = true)
  SeriesResponse toSeriesResponse(Series series);
  
  @ObjectFactory
  default Series createSeries(SaveSeries command) {
    return new Series(command.tmdbId());
  }
}
