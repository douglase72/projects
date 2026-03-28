package com.erdouglass.emdb.media.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.comand.SaveSeries;
import com.erdouglass.emdb.common.comand.UpdateSeries;
import com.erdouglass.emdb.media.dto.SaveResult;
import com.erdouglass.emdb.media.entity.Series;
import com.erdouglass.emdb.media.proto.v1.SaveSeriesRequest;
import com.erdouglass.emdb.media.proto.v1.SaveSeriesResponse;
import com.erdouglass.emdb.media.proto.v1.SeriesResponse;
import com.erdouglass.emdb.media.proto.v1.UpdateSeriesCommand;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface SeriesMapper {
  
  @Mapping(source = "backdrop.name", target = "backdrop")
  @Mapping(source = "backdrop.tmdbName", target = "tmdbBackdrop")
  @Mapping(source = "poster.name", target = "poster")
  @Mapping(source = "poster.tmdbName", target = "tmdbPoster")
  void merge(SaveSeries command, @MappingTarget Series series);
  
  void merge(UpdateSeries command, @MappingTarget Series series);
  
  @BeanMapping(builder = @Builder(disableBuilder = true))
  SaveSeries toSaveSeries(SaveSeriesRequest request);
  
  @Mapping(target = "people", ignore = true)
  @Mapping(target = "backdrop", expression = "java(series.getBackdrop() != null ? com.erdouglass.emdb.common.comand.Image.of(series.getBackdrop(), series.getTmdbBackdrop()) : null)")
  @Mapping(target = "poster", expression = "java(series.getPoster() != null ? com.erdouglass.emdb.common.comand.Image.of(series.getPoster(), series.getTmdbPoster()) : null)")
  SaveSeries toSaveSeries(Series series);
  
  @BeanMapping(builder = @Builder(disableBuilder = true))
  UpdateSeries toUpdateSeries(UpdateSeriesCommand command);
  
  @Mapping(source = "backdrop.name", target = "backdrop")
  @Mapping(source = "backdrop.tmdbName", target = "tmdbBackdrop")
  @Mapping(source = "poster.name", target = "poster")
  @Mapping(source = "poster.tmdbName", target = "tmdbPoster")
  Series toSeries(SaveSeries command);
    
  @Mapping(source = "entity", target = "series")
  SaveSeriesResponse toSaveSeriesResponse(SaveResult<Series> result);
  
  SeriesResponse toSeriesResponse(Series series);

}
