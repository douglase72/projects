package com.erdouglass.emdb.media.core.movie;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ObjectFactory;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.media.core.CommonMapper;
import com.erdouglass.emdb.media.movie.MovieDto;
import com.erdouglass.emdb.media.movie.SaveMovie;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
interface MovieMapper extends CommonMapper {
  
  @Mapping(source = "backdrop.name", target = "backdrop")
  @Mapping(source = "poster.name",   target = "poster")
  void merge(SaveMovie command, @MappingTarget Movie movie);
  
  @Mapping(source = "backdrop.name", target = "backdrop")
  @Mapping(source = "poster.name",   target = "poster")
  Movie toMovie(SaveMovie command);
  
  @Mapping(source = "backdrop", target = "backdrop", qualifiedByName = "imageToString")
  @Mapping(source = "poster",   target = "poster",   qualifiedByName = "imageToString")
  MovieDto toMovieDto(Movie movie);
  
  @ObjectFactory
  default Movie createMovie(SaveMovie command) {
    return new Movie(command.tmdbId());
  }
}
