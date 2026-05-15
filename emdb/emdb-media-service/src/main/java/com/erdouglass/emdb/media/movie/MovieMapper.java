package com.erdouglass.emdb.media.movie;

import java.util.UUID;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.api.command.SaveMovie;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface MovieMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(source = "backdrop.name",     target = "backdrop")
  @Mapping(source = "backdrop.tmdbName", target = "tmdbBackdrop")
  @Mapping(source = "poster.name",       target = "poster")
  @Mapping(source = "poster.tmdbName",   target = "tmdbPoster")
  Movie toMovie(final SaveMovie command);
  
  @Mapping(source = "backdrop", target = "backdrop", qualifiedByName = "imageToString")
  @Mapping(source = "poster",   target = "poster",   qualifiedByName = "imageToString")
  MovieDto toMovieDto(final Movie movie);
  
  @Named("imageToString")
  default String imageToString(final UUID image) {
    return image != null ? image.toString() + ".jpg" : null;
  } 
}
