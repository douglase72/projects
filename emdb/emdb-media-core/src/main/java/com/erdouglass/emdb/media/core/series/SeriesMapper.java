package com.erdouglass.emdb.media.core.series;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ObjectFactory;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.media.core.CommonMapper;
import com.erdouglass.emdb.media.series.SaveSeries;
import com.erdouglass.emdb.media.series.SeriesDto;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
interface SeriesMapper extends CommonMapper {

  @Mapping(target = "firstAirDate", ignore = true)
  @Mapping(source = "backdrop.name", target = "backdrop")
  @Mapping(source = "poster.name",   target = "poster")
  void merge(SaveSeries command, @MappingTarget Series series);
  
  @Mapping(target = "firstAirDate", ignore = true)
  @Mapping(source = "backdrop.name", target = "backdrop")
  @Mapping(source = "poster.name",   target = "poster")
  Series toSeries(SaveSeries command);
  
  @Mapping(target = "lastAirDate", ignore = true)
  @Mapping(source = "backdrop", target = "backdrop", qualifiedByName = "imageToString")
  @Mapping(source = "poster",   target = "poster",   qualifiedByName = "imageToString")
  SeriesDto toSeriesDto(Series series);
  
  @ObjectFactory
  default Series createMovie(SaveSeries command) {
    return new Series(command.tmdbId());
  }
}
