package com.erdouglass.emdb.gateway.mapper;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.comand.SaveSeries;
import com.erdouglass.emdb.common.comand.UpdateSeries;
import com.erdouglass.emdb.common.query.SeriesDetails;
import com.erdouglass.emdb.common.query.SeriesView;
import com.erdouglass.emdb.gateway.query.Slice;
import com.erdouglass.emdb.gateway.query.SeriesQueryParams;
import com.erdouglass.emdb.media.proto.v1.FindAllSeriesRequest;
import com.erdouglass.emdb.media.proto.v1.FindRequest;
import com.erdouglass.emdb.media.proto.v1.SaveSeriesRequest;
import com.erdouglass.emdb.media.proto.v1.SeriesPageResponse;
import com.erdouglass.emdb.media.proto.v1.SeriesResponse;
import com.erdouglass.emdb.media.proto.v1.UpdateSeriesRequest;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS 
)
public interface SeriesMapper extends CommonMapper {

  // Request
  SaveSeriesRequest toSaveSeriesRequest(SaveSeries command);
  
  FindRequest toFindRequest(Long id, String append);
  
  FindAllSeriesRequest toFindAllSeriesRequest(SeriesQueryParams parameters);
  
  @Mapping(target = "id", source = "id")
  @Mapping(target = "command", source = "command")
  UpdateSeriesRequest toUpdateSeriesRequest(Long id, UpdateSeries command);
  
  // Response
  @ShowImageMapping
  SeriesDetails toSeriesDetails(SeriesResponse response);
  
  Slice<SeriesView> toPage(SeriesPageResponse response);
}
