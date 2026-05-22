package com.erdouglass.emdb.media.movie;

import java.util.UUID;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.movie.SaveMovie;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
interface MovieMapper {
  
  void merge(Movie source, @MappingTarget Movie target);

  @Mapping(target = "id",       ignore = true)
  @Mapping(target = "backdrop", ignore = true)
  @Mapping(target = "poster",   ignore = true)
  Movie toMovie(SaveMovie command);
  
  @Mapping(source = "backdrop", target = "backdrop", qualifiedByName = "uuidToImage")
  @Mapping(source = "poster",   target = "poster",   qualifiedByName = "uuidToImage")
  MovieResponse toMovieResponse(Movie movie);
  
  @Named("uuidToImage")
  default String uuidToImage(UUID image) {
    return image.toString() + ".jpg";
  }
}
