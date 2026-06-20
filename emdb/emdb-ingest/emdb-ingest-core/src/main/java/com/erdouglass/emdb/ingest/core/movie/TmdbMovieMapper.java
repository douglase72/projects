package com.erdouglass.emdb.ingest.core.movie;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.ingest.ws.rest.TmdbMovie;
import com.erdouglass.emdb.media.Image;
import com.erdouglass.emdb.media.command.SaveMovie;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
interface TmdbMovieMapper {

  @BeanMapping(builder = @Builder(disableBuilder = true))
  @Mapping(source = "movie.id", target = "tmdbId")
  @Mapping(source = "movie.release_date", target = "releaseDate")
  @Mapping(source = "movie.vote_average", target = "score")
  @Mapping(source = "movie.original_language", target = "originalLanguage")
  SaveMovie toSaveMovie(TmdbMovie movie, Image backdrop, Image poster);
}
