package com.erdouglass.emdb.ingest.core.movie;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.media.image.Image;
import com.erdouglass.emdb.media.movie.SaveMovie;
import com.erdouglass.emdb.media.movie.SaveMovie.CastCredit;
import com.erdouglass.emdb.media.movie.SaveMovie.CrewCredit;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
interface TmdbMovieMapper {
  
  @BeanMapping(builder = @Builder(disableBuilder = true))
  @Mapping(source = "movie.id",                target = "tmdbId")
  @Mapping(source = "movie.release_date",      target = "releaseDate")
  @Mapping(source = "movie.vote_average",      target = "score")
  @Mapping(source = "movie.original_language", target = "originalLanguage")
  SaveMovie toSaveMovie(TmdbMovie movie, Image backdrop, Image poster);
  
  @Mapping(source = "credit_id",    target = "creditId")
  @Mapping(source = "id",           target = "tmdbId")
  @Mapping(source = "profile_path", target = "profile")
  CastCredit toCastCredit(TmdbMovie.CastCredit credit);
  
  @Mapping(source = "credit_id",    target = "creditId")
  @Mapping(source = "id",           target = "tmdbId")
  @Mapping(source = "profile_path", target = "profile")
  CrewCredit toCastCredit(TmdbMovie.CrewCredit credit);
}
