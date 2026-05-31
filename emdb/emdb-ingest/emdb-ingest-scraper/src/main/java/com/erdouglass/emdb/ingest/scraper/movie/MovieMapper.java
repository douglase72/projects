package com.erdouglass.emdb.ingest.scraper.movie;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.ingest.scraper.movie.Movie.CastCredit;
import com.erdouglass.emdb.ingest.scraper.movie.Movie.CrewCredit;
import com.erdouglass.emdb.media.Image;
import com.erdouglass.emdb.media.movie.SaveMovie;
import com.erdouglass.emdb.media.movie.SaveMovie.CastMember;
import com.erdouglass.emdb.media.movie.SaveMovie.CrewMember;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
interface MovieMapper {
  
  @Mapping(source = "id", target = "tmdbId")
  @Mapping(source = "profile_path", target = "profile")
  CastMember toCastCredit(CastCredit credit);

  @Mapping(source = "id", target = "tmdbId")
  @Mapping(source = "profile_path", target = "profile")
  CrewMember toCrewCredit(CrewCredit credit);

  @BeanMapping(builder = @Builder(disableBuilder = true))
  @Mapping(source = "movie.id", target = "tmdbId")
  @Mapping(source = "movie.release_date", target = "releaseDate")
  @Mapping(source = "movie.vote_average", target = "score")
  @Mapping(source = "movie.original_language", target = "originalLanguage")
  SaveMovie toSaveMovie(Movie movie, Image backdrop, Image poster);
}
