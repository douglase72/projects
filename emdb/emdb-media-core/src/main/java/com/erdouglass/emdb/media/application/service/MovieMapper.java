package com.erdouglass.emdb.media.application.service;

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

import com.erdouglass.emdb.media.SaveMovie;
import com.erdouglass.emdb.media.application.port.inbound.MovieView;
import com.erdouglass.emdb.media.application.port.inbound.MovieView.MovieCastCredit;
import com.erdouglass.emdb.media.application.port.inbound.MovieView.MovieCredits;
import com.erdouglass.emdb.media.application.port.inbound.MovieView.MovieCrewCredit;
import com.erdouglass.emdb.media.domain.movie.Movie;
import com.erdouglass.emdb.media.domain.movie.MovieCreditProjection;
import com.erdouglass.emdb.media.domain.shared.Credit.CreditType;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
interface MovieMapper extends CommonMapper {

  @Mapping(source = "backdrop.name", target = "backdrop")
  @Mapping(source = "poster.name",   target = "poster")
  void merge(SaveMovie command, @MappingTarget Movie movie);
  
  @Mapping(source = "backdrop.name", target = "backdrop")
  @Mapping(source = "poster.name",   target = "poster")
  Movie toMovie(SaveMovie command);
  
  @Mapping(source = "backdrop", target = "backdrop", qualifiedByName = "imageToString")
  @Mapping(source = "poster",   target = "poster",   qualifiedByName = "imageToString")
  MovieView toMovieView(Movie movie);
  
  @Mapping(source = "personId",    target = "id")
  @Mapping(source = "profile",     target = "profile", qualifiedByName = "imageToString")
  @Mapping(source = "role",        target = "character")
  @Mapping(source = "creditOrder", target = "order")
  MovieCastCredit toCastCredit(MovieCreditProjection credit);

  @Mapping(source = "personId",   target = "id")
  @Mapping(source = "profile",    target = "profile", qualifiedByName = "imageToString")
  @Mapping(source = "role",       target = "job")
  MovieCrewCredit toCrewCredit(MovieCreditProjection credit);

  default MovieCredits toCredits(List<MovieCreditProjection> credits) {
    var cast = new ArrayList<MovieCastCredit>();
    var crew = new ArrayList<MovieCrewCredit>();
    for (var credit : credits) {
      if (credit.type() == CreditType.CAST) {
        cast.add(toCastCredit(credit));
      } else {
        crew.add(toCrewCredit(credit));
      }
    }
    return new MovieCredits(cast, crew);
  }
  
  @ObjectFactory
  default Movie createMovie(SaveMovie command) {
    return new Movie(command.externalId());
  }
}
