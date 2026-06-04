package com.erdouglass.emdb.media.movie;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.media.Image;
import com.erdouglass.emdb.media.command.SaveMovie;
import com.erdouglass.emdb.media.query.MovieResponse;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
interface MovieMapper {
  
  @Mapping(source = "command.backdrop.tmdbName", target = "tmdbBackdrop")
  @Mapping(source = "command.backdrop.emdbName", target = "backdrop")
  @Mapping(source = "command.poster.tmdbName",   target = "tmdbPoster")
  @Mapping(source = "command.poster.emdbName",   target = "poster")
  void merge(SaveMovie command, @MappingTarget Movie movie);

  @Mapping(source = "backdrop.tmdbName", target = "tmdbBackdrop")
  @Mapping(source = "backdrop.emdbName", target = "backdrop")
  @Mapping(source = "poster.tmdbName",   target = "tmdbPoster")
  @Mapping(source = "poster.emdbName",   target = "poster")
  Movie toMovie(SaveMovie command, Image backdrop, Image poster);
  
  MovieResponse toMovieResponse(Movie movie);
}
