package com.erdouglass.emdb.scraper.mapper;

import java.util.List;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.comand.Image;
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
  @Mapping(source = "credits", target = "credits")
  SaveMovie toSaveMovie(TmdbMovie movie, Image backdrop, Image poster, Credits credits, List<SavePerson> people);
  
}
