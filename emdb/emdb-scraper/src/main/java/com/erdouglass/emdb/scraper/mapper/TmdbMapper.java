package com.erdouglass.emdb.scraper.mapper;

import java.util.UUID;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.scraper.query.TmdbMovieDto;

@Mapper(
    componentModel = "cdi", 
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    builder = @Builder(disableBuilder = true)
)
public interface TmdbMapper {

  @Mapping(source = "movie.id", target = "tmdbId")
  @Mapping(source = "movie.release_date", target = "releaseDate")
  @Mapping(source = "movie.vote_average", target = "score")
  @Mapping(source = "movie.original_language", target = "originalLanguage")
  @Mapping(source = "backdrop", target = "backdrop")
  @Mapping(source = "poster", target = "poster")
  SaveMovie toSaveMovie(TmdbMovieDto movie, UUID backdrop, UUID poster);
  
}
