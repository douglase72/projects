package com.erdouglass.emdb.media.application.service;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ObjectFactory;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.media.SaveMovie;
import com.erdouglass.emdb.media.domain.movie.Movie;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
interface MovieMapper {

  @Mapping(source = "backdrop.name", target = "backdrop")
  @Mapping(source = "poster.name",   target = "poster")
  void merge(SaveMovie command, @MappingTarget Movie movie);
  
  @Mapping(source = "backdrop.name", target = "backdrop")
  @Mapping(source = "poster.name",   target = "poster")
  Movie toMovie(SaveMovie command);
  
  @ObjectFactory
  default Movie createMovie(SaveMovie command) {
    return new Movie(command.externalId());
  }
}
