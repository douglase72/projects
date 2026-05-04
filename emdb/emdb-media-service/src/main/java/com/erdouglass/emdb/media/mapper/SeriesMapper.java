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

import com.erdouglass.emdb.media.api.Image;
import com.erdouglass.emdb.media.api.command.SaveSeries;
import com.erdouglass.emdb.media.api.command.UpdateSeries;
import com.erdouglass.emdb.media.api.query.SeriesQueryParameters;
import com.erdouglass.emdb.media.api.query.SeriesView;
import com.erdouglass.emdb.media.entity.Series;
import com.erdouglass.emdb.media.proto.v1.FindAllSeriesRequest;
import com.erdouglass.emdb.media.proto.v1.SaveSeriesRequest;
import com.erdouglass.emdb.media.proto.v1.SaveSeriesResponse;
import com.erdouglass.emdb.media.proto.v1.SeriesPageResponse;
import com.erdouglass.emdb.media.proto.v1.SeriesResponse;
import com.erdouglass.emdb.media.proto.v1.SeriesViewResponse;
import com.erdouglass.emdb.media.proto.v1.UpdateSeriesCommand;
import com.erdouglass.emdb.media.query.SaveResult;
import com.erdouglass.emdb.media.query.TmdbSeries;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface SeriesMapper extends CommonMapper {
  
  // Request
  
  @ShowImageMapping
  @Mapping(target = "firstAirDate", ignore = true)
  void merge(SaveSeries command, @MappingTarget Series series);
  
  @Mapping(target = "tmdbId", ignore = true)
  @Mapping(target = "firstAirDate", ignore = true)
  @Mapping(target = "tmdbBackdrop", ignore = true)
  @Mapping(target = "tmdbPoster", ignore = true)
  void merge(UpdateSeries command, @MappingTarget Series series);

  @BeanMapping(builder = @Builder(disableBuilder = true))
  SaveSeries toSaveSeries(SaveSeriesRequest request);
  
  @BeanMapping(builder = @Builder(disableBuilder = true))
  @Mapping(source = "series", target = "backdrop", qualifiedByName = "backdropToImage")
  @Mapping(source = "series", target = "poster", qualifiedByName = "posterToImage")
  SaveSeries toSaveSeries(Series series);
  
  @BeanMapping(builder = @Builder(disableBuilder = true))
  @Mapping(source = "series.id", target = "tmdbId")
  @Mapping(source = "series.name", target = "title")
  @Mapping(source = "series.vote_average", target = "score")
  @Mapping(source = "series.original_language", target = "originalLanguage")
  SaveSeries toSaveSeries(TmdbSeries series, Image backdrop, Image poster);
  
  @InheritConfiguration(name = "merge")
  Series toSeries(SaveSeries command);
  
  SeriesQueryParameters toSeriesQueryParameters(FindAllSeriesRequest request);
  
  @BeanMapping(builder = @Builder(disableBuilder = true))
  UpdateSeries toUpdateSeries(UpdateSeriesCommand command);
  
  // Response 
  
  @Mapping(source = "entity", target = "series")
  SaveSeriesResponse toSaveSeriesResponse(SaveResult<Series> result);
  
  @Mapping(target = "lastAirDate", ignore = true)
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
