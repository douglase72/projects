package com.erdouglass.emdb.ingest.scraper.movie;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.ingest.scraper.movie.Movie.TmdbCastCredit;
import com.erdouglass.emdb.ingest.scraper.movie.Movie.TmdbCrewCredit;
import com.erdouglass.emdb.media.Image;
import com.erdouglass.emdb.media.movie.SaveMovie;
import com.erdouglass.emdb.media.movie.SaveMovie.CastCredit;
import com.erdouglass.emdb.media.movie.SaveMovie.CrewCredit;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
interface MovieMapper {

  @BeanMapping(builder = @Builder(disableBuilder = true))
  @Mapping(source = "movie.id", target = "tmdbId")
  @Mapping(source = "movie.release_date", target = "releaseDate")
  @Mapping(source = "movie.vote_average", target = "score")
  @Mapping(source = "movie.original_language", target = "originalLanguage")
  SaveMovie toSaveMovie(Movie movie, Image backdrop, Image poster);
  
  @Mapping(source = "id", target = "tmdbId")
  @Mapping(source = "profile_path", target = "profile")
  CastCredit toCastCredit(TmdbCastCredit credit);
  
  @Mapping(source = "id", target = "tmdbId")
  @Mapping(source = "profile_path", target = "profile")
  CrewCredit toCastCredit(TmdbCrewCredit credit);
}
