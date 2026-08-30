package com.erdouglass.emdb.ingest.adapter.out.media;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.ingest.application.port.out.Movie;
import com.erdouglass.emdb.ingest.domain.model.TmdbId;
import com.erdouglass.emdb.media.api.LoadMovieCommand;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
interface CommandMapper {

  @Mapping(source = "tmdbId", target = "tmdbId", qualifiedByName = "toValue")
  LoadMovieCommand toSaveMovieCommand(Movie movie);
  
  @Named("toValue")
  default Integer toTmdbId(TmdbId tmdbId) {
    return tmdbId.value();
  }
}
