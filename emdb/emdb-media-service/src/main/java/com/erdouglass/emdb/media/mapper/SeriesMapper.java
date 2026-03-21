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
import com.erdouglass.emdb.media.dto.SaveResult;
import com.erdouglass.emdb.media.entity.Series;
import com.erdouglass.emdb.media.proto.v1.SaveSeriesRequest;
import com.erdouglass.emdb.media.proto.v1.SaveSeriesResponse;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface SeriesMapper {
  
  void merge(SaveSeries command, @MappingTarget Series series);
  
  @BeanMapping(builder = @Builder(disableBuilder = true))
  SaveSeries toSaveSeries(SaveSeriesRequest request);
  
  Series toSeries(SaveSeries command);
    
  @Mapping(source = "entity", target = "series")
  SaveSeriesResponse toSaveSeriesResponse(SaveResult<Series> result);

}
