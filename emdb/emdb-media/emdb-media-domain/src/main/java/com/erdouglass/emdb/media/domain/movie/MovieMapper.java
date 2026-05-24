package com.erdouglass.emdb.media.domain.movie;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.media.api.command.SaveMovie;
import com.erdouglass.emdb.media.api.query.MovieResponse;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
interface MovieMapper {
  
  @Mapping(target = "backdrop", ignore = true)
  @Mapping(target = "poster",   ignore = true)
  void merge(SaveMovie command, @MappingTarget Movie movie);

  @Mapping(target = "backdrop", ignore = true)
  @Mapping(target = "poster",   ignore = true)
  Movie toMovie(SaveMovie command);
  
  MovieResponse toMovieResponse(Movie movie);
}
