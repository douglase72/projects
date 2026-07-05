package com.erdouglass.emdb.media.core.movie;

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

import com.erdouglass.emdb.media.core.CommonMapper;
import com.erdouglass.emdb.media.core.credit.CreditType;
import com.erdouglass.emdb.media.movie.MovieDto;
import com.erdouglass.emdb.media.movie.MovieDto.MovieCastCredit;
import com.erdouglass.emdb.media.movie.MovieDto.MovieCredits;
import com.erdouglass.emdb.media.movie.MovieDto.MovieCrewCredit;
import com.erdouglass.emdb.media.movie.SaveMovie;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
interface MovieMapper extends CommonMapper {
  
  @Mapping(target = "credits",       ignore = true)
  @Mapping(source = "backdrop.name", target = "backdrop")
  @Mapping(source = "poster.name",   target = "poster")
  void merge(SaveMovie command, @MappingTarget Movie movie);
  
  @Mapping(target = "credits",       ignore = true)
  @Mapping(source = "backdrop.name", target = "backdrop")
  @Mapping(source = "poster.name",   target = "poster")
  Movie toMovie(SaveMovie command);
  
  @Mapping(target = "credits",  ignore = true)
  @Mapping(source = "backdrop", target = "backdrop", qualifiedByName = "imageToString")
  @Mapping(source = "poster",   target = "poster",   qualifiedByName = "imageToString")
  MovieDto toMovieView(Movie movie);
  
  @Mapping(source = "backdrop", target = "backdrop", qualifiedByName = "imageToString")
  @Mapping(source = "poster",   target = "poster",   qualifiedByName = "imageToString")
  MovieDto toMovieDto(Movie movie);
  
  @Mapping(source = "id",             target = "creditId")
  @Mapping(source = "person.id",      target = "id")
  @Mapping(source = "person.name",    target = "name")
  @Mapping(source = "person.gender",  target = "gender")
  @Mapping(source = "person.profile", target = "profile", qualifiedByName = "imageToString")
  @Mapping(source = "role",           target = "character")
  MovieCastCredit toCastCredit(MovieCredit credit);
  
  @Mapping(source = "id",             target = "creditId")
  @Mapping(source = "person.id",      target = "id")
  @Mapping(source = "person.name",    target = "name")
  @Mapping(source = "person.gender",  target = "gender")
  @Mapping(source = "person.profile", target = "profile", qualifiedByName = "imageToString")
  @Mapping(source = "role",           target = "job")
  MovieCrewCredit toCrewCredit(MovieCredit credit);
  
  default MovieCredits toCredits(List<MovieCredit> credits) {
    if (credits == null) {
      return null;
    }
    var cast = new ArrayList<MovieCastCredit>();
    var crew = new ArrayList<MovieCrewCredit>();
    for (MovieCredit credit : credits) {
      if (credit.getType() == CreditType.CAST) {
        cast.add(toCastCredit(credit));
      } else {
        crew.add(toCrewCredit(credit));
      }
    }
    return new MovieCredits(cast, crew);
  }
  
  @ObjectFactory
  default Movie createMovie(SaveMovie command) {
    return new Movie(command.tmdbId());
  }
}
