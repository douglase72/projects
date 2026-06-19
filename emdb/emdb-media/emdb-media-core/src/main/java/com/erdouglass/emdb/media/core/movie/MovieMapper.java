package com.erdouglass.emdb.media.core.movie;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ObjectFactory;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.media.command.SaveMovie;
import com.erdouglass.emdb.media.query.MovieDto;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
interface MovieMapper {
  
  @Mapping(source = "backdrop.name", target = "backdrop")
  @Mapping(source = "backdrop.name", target = "poster")
  Movie toMovie(SaveMovie command);
  
  MovieDto toMovieDto(Movie movie);
  
  @ObjectFactory
  default Movie createMovie(SaveMovie command) {
    return new Movie(command.tmdbId());
  }
}
