package com.erdouglass.emdb.scraper.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.Image;
import com.erdouglass.emdb.common.comand.SaveSeries;
import com.erdouglass.emdb.scraper.query.TmdbSeries;

@Mapper(
    componentModel = "cdi", 
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface TmdbSeriesMapper {

  @BeanMapping(builder = @Builder(disableBuilder = true))
  @Mapping(source = "series.id", target = "tmdbId")
  @Mapping(source = "series.name", target = "title")
  @Mapping(source = "series.vote_average", target = "score")
  @Mapping(source = "series.original_language", target = "originalLanguage")
  SaveSeries toSaveSeries(TmdbSeries series, Image backdrop, Image poster);
}
