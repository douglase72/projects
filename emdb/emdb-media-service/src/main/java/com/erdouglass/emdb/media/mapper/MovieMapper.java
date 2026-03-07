package com.erdouglass.emdb.media.mapper;

import java.util.UUID;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.common.comand.UpdateMovie;
import com.erdouglass.emdb.common.query.MovieDto;
import com.erdouglass.emdb.media.entity.Movie;

/// MapStruct mapper for converting between `Movie` entities, commands, and DTOs.
///
/// Configured with `disableBuilder = true` to prevent MapStruct from incorrectly
/// using the partial DTO builder. This forces MapStruct to use the complete 
/// canonical record constructor for data transfer objects.
@Mapper(
    componentModel = "cdi", 
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    builder = @Builder(disableBuilder = true)
)
public interface MovieMapper {
  
  void merge(SaveMovie command, @MappingTarget Movie movie);
  
  @Mapping(target = "tmdbBackdrop", ignore = true)
  @Mapping(target = "tmdbPoster", ignore = true)
  @Mapping(target = "tmdbId", ignore = true)
  void merge(UpdateMovie command, @MappingTarget Movie movie);
  
  Movie toMovie(SaveMovie command);
  
  @Mapping(target = "backdrop", qualifiedByName = "toFilename")
  @Mapping(target = "poster", qualifiedByName = "toFilename")  
  MovieDto toMovieDto(Movie movie);
  
  SaveMovie toSaveMovie(Movie movie);
  
  @Named("toFilename")
  default String toFilename(UUID filename) {
    if (filename == null) {
      return null;
    }    
    return filename + ".jpg";
  }  
  
}
