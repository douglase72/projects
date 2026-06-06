package com.erdouglass.emdb.media.movie;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ObjectFactory;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.media.Image;
import com.erdouglass.emdb.media.command.SaveMovie;
import com.erdouglass.emdb.media.credit.CreditType;
import com.erdouglass.emdb.media.internal.CommonMapper;
import com.erdouglass.emdb.media.query.MovieResponse;
import com.erdouglass.emdb.media.query.MovieResponse.CastCredit;
import com.erdouglass.emdb.media.query.MovieResponse.Credits;
import com.erdouglass.emdb.media.query.MovieResponse.CrewCredit;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
interface MovieMapper extends CommonMapper {
  
  @Mapping(target = "credits", ignore = true)
  @Mapping(source = "command.backdrop.tmdbName", target = "tmdbBackdrop")
  @Mapping(source = "command.backdrop.emdbName", target = "backdrop")
  @Mapping(source = "command.poster.tmdbName",   target = "tmdbPoster")
  @Mapping(source = "command.poster.emdbName",   target = "poster")
  void merge(SaveMovie command, @MappingTarget Movie movie);

  @Mapping(target = "credits", ignore = true)
  @Mapping(source = "backdrop.tmdbName", target = "tmdbBackdrop")
  @Mapping(source = "backdrop.emdbName", target = "backdrop")
  @Mapping(source = "poster.tmdbName",   target = "tmdbPoster")
  @Mapping(source = "poster.emdbName",   target = "poster")
  Movie toMovie(SaveMovie command, Image backdrop, Image poster);
  
  @Mapping(source = "backdrop", target = "backdrop", qualifiedByName = "imageToString")
  @Mapping(source = "poster",   target = "poster",   qualifiedByName = "imageToString")
  MovieResponse toMovieResponse(Movie movie);
  
  @Mapping(target = "credits", ignore = true)
  @Mapping(source = "backdrop", target = "backdrop", qualifiedByName = "imageToString")
  @Mapping(source = "poster",   target = "poster",   qualifiedByName = "imageToString")
  MovieResponse toMovieSummary(Movie movie);
  
  default Credits toCredits(List<MovieCredit> credits) {
    if (credits == null) {
      return null;
    }
    var cast = new ArrayList<CastCredit>();
    var crew = new ArrayList<CrewCredit>();
    for (MovieCredit credit : credits) {
      if (credit.getType() == CreditType.CAST) {
        cast.add(toCastCredit(credit));
      } else {
        crew.add(toCrewCredit(credit));
      }
    }
    return new Credits(cast, crew);
  }
  
  @Mapping(source = "id",             target = "creditId")
  @Mapping(source = "person.id",      target = "id")
  @Mapping(source = "person.name",    target = "name")
  @Mapping(source = "person.gender",  target = "gender")
  @Mapping(source = "person.profile", target = "profile", qualifiedByName = "imageToString")
  @Mapping(source = "role",           target = "character")
  CastCredit toCastCredit(MovieCredit credit);
  
  @Mapping(source = "id",             target = "creditId")
  @Mapping(source = "person.id",      target = "id")
  @Mapping(source = "person.name",    target = "name")
  @Mapping(source = "person.gender",  target = "gender")
  @Mapping(source = "person.profile", target = "profile", qualifiedByName = "imageToString")
  @Mapping(source = "role",           target = "job")
  CrewCredit toCrewCredit(MovieCredit credit);
  
  @ObjectFactory
  default Movie createMovie(SaveMovie command) {
    return new Movie(command.tmdbId());
  }
}
