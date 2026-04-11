package com.erdouglass.emdb.media.mapper;

import jakarta.data.page.Page;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.comand.SaveSeries;
import com.erdouglass.emdb.common.comand.UpdateSeries;
import com.erdouglass.emdb.common.query.SeriesQueryParameters;
import com.erdouglass.emdb.common.query.SeriesView;
import com.erdouglass.emdb.media.dto.SaveResult;
import com.erdouglass.emdb.media.entity.Series;
import com.erdouglass.emdb.media.proto.v1.FindAllSeriesRequest;
import com.erdouglass.emdb.media.proto.v1.SaveSeriesRequest;
import com.erdouglass.emdb.media.proto.v1.SaveSeriesResponse;
import com.erdouglass.emdb.media.proto.v1.SeriesPageResponse;
import com.erdouglass.emdb.media.proto.v1.SeriesResponse;
import com.erdouglass.emdb.media.proto.v1.SeriesViewResponse;
import com.erdouglass.emdb.media.proto.v1.UpdateSeriesCommand;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface SeriesMapper extends ShowMapper {
  
  // Request
  
  @ImageMapping
  void merge(SaveSeries command, @MappingTarget Series series);
  void merge(UpdateSeries command, @MappingTarget Series series);

  @BeanMapping(builder = @Builder(disableBuilder = true))
  SaveSeries toSaveSeries(SaveSeriesRequest request);
  
  @InheritConfiguration(name = "merge")
  Series toSeries(SaveSeries command);
  
  SeriesQueryParameters toSeriesQueryParameters(FindAllSeriesRequest request);
  
  @BeanMapping(builder = @Builder(disableBuilder = true))
  UpdateSeries toUpdateSeries(UpdateSeriesCommand command);
  
  // Response 
  
  @Mapping(source = "entity", target = "series")
  SaveSeriesResponse toSaveSeriesResponse(SaveResult<Series> result);
  
  SeriesResponse toSeriesResponse(Series series);
  
  SeriesViewResponse toSeriesViewResponse(SeriesView view);
  
  default SeriesPageResponse toSeriesPageResponse(Page<SeriesView> page) {
    var builder = SeriesPageResponse.newBuilder()
        .setPage((int) page.pageRequest().page())
        .setSize(page.pageRequest().size())
        .setHasNext(page.hasNext());
    page.content().stream()
        .map(this::toSeriesViewResponse)
        .forEach(builder::addResults);
    return builder.build();
  }
}
