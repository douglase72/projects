package com.erdouglass.emdb.ingest.movie;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.command.SaveMovie;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
interface MovieMapper {

  @BeanMapping(builder = @Builder(disableBuilder = true))
  @Mapping(source = "id", target = "tmdbId")
  @Mapping(source = "release_date", target = "releaseDate")
  @Mapping(source = "vote_average", target = "score")
  @Mapping(source = "backdrop_path", target = "tmdbBackdrop")
  @Mapping(source = "poster_path", target = "tmdbPoster")
  @Mapping(source = "original_language", target = "originalLanguage")
  SaveMovie toSaveMovie(Movie movie);
}
