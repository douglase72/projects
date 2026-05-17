package com.erdouglass.emdb.media.movie;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.command.SaveMovie;
import com.erdouglass.emdb.common.command.SaveMovieResponse;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface MovieMapper {
  
  void merge(Movie source, @MappingTarget Movie target);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "backdrop", ignore = true)
  @Mapping(target = "poster", ignore = true)
  Movie toMovie(SaveMovie command);
  
  @Mapping(target = "backdrop.name",     source = "backdrop")
  @Mapping(target = "backdrop.tmdbName", source = "tmdbBackdrop")
  @Mapping(target = "poster.name",       source = "poster")
  @Mapping(target = "poster.tmdbName",   source = "tmdbPoster")
  SaveMovieResponse toSaveMovieResponse(Movie movie);
}
