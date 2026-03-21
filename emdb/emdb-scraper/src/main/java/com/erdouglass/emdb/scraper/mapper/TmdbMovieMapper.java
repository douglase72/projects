package com.erdouglass.emdb.scraper.mapper;

import java.util.List;
import java.util.UUID;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.common.comand.SaveMovie.Credits;
import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.scraper.query.TmdbMovie;

@Mapper(
    componentModel = "cdi", 
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    builder = @Builder(disableBuilder = true)
)
public interface TmdbMovieMapper {
  
  @Mapping(source = "id", target = "tmdbId")
  SaveMovie.CastCredit toCastCredit(TmdbMovie.CastCredit credit);

  @Mapping(source = "id", target = "tmdbId")
  SaveMovie.CrewCredit toCrewCredit(TmdbMovie.CrewCredit credit);

  @Mapping(source = "movie.id", target = "tmdbId")
  @Mapping(source = "movie.release_date", target = "releaseDate")
  @Mapping(source = "movie.vote_average", target = "score")
  @Mapping(source = "movie.original_language", target = "originalLanguage")
  @Mapping(source = "backdrop", target = "backdrop")
  @Mapping(source = "poster", target = "poster")
  @Mapping(source = "credits", target = "credits")
  SaveMovie toSaveMovie(TmdbMovie movie, UUID backdrop, UUID poster, Credits credits, List<SavePerson> people);
  
}
