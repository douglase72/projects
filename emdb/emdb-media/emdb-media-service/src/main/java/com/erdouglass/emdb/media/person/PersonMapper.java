package com.erdouglass.emdb.media.person;

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
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;

import com.erdouglass.emdb.media.Image;
import com.erdouglass.emdb.media.command.SavePerson;
import com.erdouglass.emdb.media.credit.Credit;
import com.erdouglass.emdb.media.internal.CommonMapper;
import com.erdouglass.emdb.media.movie.MovieCredit;
import com.erdouglass.emdb.media.query.Job;
import com.erdouglass.emdb.media.query.PersonCastCredit;
import com.erdouglass.emdb.media.query.PersonCrewCredit;
import com.erdouglass.emdb.media.query.PersonMovieCastCredit;
import com.erdouglass.emdb.media.query.PersonMovieCrewCredit;
import com.erdouglass.emdb.media.query.PersonResponse;
import com.erdouglass.emdb.media.query.PersonResponse.Credits;
import com.erdouglass.emdb.media.query.PersonSeriesCastCredit;
import com.erdouglass.emdb.media.query.PersonSeriesCrewCredit;
import com.erdouglass.emdb.media.query.Role;
import com.erdouglass.emdb.media.series.SeriesCredit;

@Mapper(
    componentModel = "cdi",
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION
)
interface PersonMapper extends CommonMapper {

  @Mapping(target = "credits", ignore = true)
  @Mapping(source = "profile.tmdbName", target = "tmdbProfile")
  @Mapping(source = "profile.emdbName", target = "profile")
  void merge(SavePerson command, @MappingTarget Person person);

  @Mapping(target = "credits", ignore = true)
  @Mapping(source = "profile.tmdbName", target = "tmdbProfile")
  @Mapping(source = "profile.emdbName", target = "profile")
  Person toPerson(SavePerson command, Image profile);

  @Mapping(target = "credits", ignore = true)
  @Mapping(source = "profile", target = "profile", qualifiedByName = "imageToString")
  PersonResponse toPersonResponse(Person person);

  default Credits toCredits(List<Credit> credits) {
    if (credits == null) {
      return null;
    }
    var cast = new ArrayList<PersonCastCredit>();
    var crew = new ArrayList<PersonCrewCredit>();
    for (var credit : credits) {
      switch (credit.getType()) {
        case CAST -> cast.add(toCastCredit(credit));
        case CREW -> crew.add(toCrewCredit(credit));
      }
    }
    return new Credits(cast, crew);
  }

  @SubclassMapping(source = MovieCredit.class,  target = PersonMovieCastCredit.class)
  @SubclassMapping(source = SeriesCredit.class, target = PersonSeriesCastCredit.class)
  PersonCastCredit toCastCredit(Credit credit);

  @SubclassMapping(source = MovieCredit.class,  target = PersonMovieCrewCredit.class)
  @SubclassMapping(source = SeriesCredit.class, target = PersonSeriesCrewCredit.class)
  PersonCrewCredit toCrewCredit(Credit credit);

  @Mapping(target = "creditId",    source = "id")             
  @Mapping(target = "id",          source = "movie.id")     
  @Mapping(target = "title",       source = "movie.title")
  @Mapping(target = "releaseDate", source = "movie.releaseDate")
  @Mapping(target = "score",       source = "movie.score")
  @Mapping(target = "backdrop",    source = "movie.backdrop", qualifiedByName = "imageToString")
  @Mapping(target = "poster",      source = "movie.poster",   qualifiedByName = "imageToString")
  @Mapping(target = "overview",    source = "movie.overview")
  @Mapping(target = "character",   source = "role")
  @Mapping(target = "type",        constant = "MOVIE")
  PersonMovieCastCredit toPersonMovieCastCredit(MovieCredit credit);

  @Mapping(target = "creditId",     source = "id")
  @Mapping(target = "id",           source = "series.id")
  @Mapping(target = "title",        source = "series.title")
  @Mapping(target = "firstAirDate", source = "series.firstAirDate")
  @Mapping(target = "score",        source = "series.score")
  @Mapping(target = "backdrop",     source = "series.backdrop", qualifiedByName = "imageToString")
  @Mapping(target = "poster",       source = "series.poster",   qualifiedByName = "imageToString")
  @Mapping(target = "overview",     source = "series.overview")
  @Mapping(target = "type",         constant = "SERIES")
  PersonSeriesCastCredit toPersonSeriesCastCredit(SeriesCredit credit);

  @Mapping(target = "creditId",    source = "id")
  @Mapping(target = "id",          source = "movie.id")
  @Mapping(target = "title",       source = "movie.title")
  @Mapping(target = "releaseDate", source = "movie.releaseDate")
  @Mapping(target = "score",       source = "movie.score")
  @Mapping(target = "backdrop",    source = "movie.backdrop", qualifiedByName = "imageToString")
  @Mapping(target = "poster",      source = "movie.poster",   qualifiedByName = "imageToString")
  @Mapping(target = "overview",    source = "movie.overview")
  @Mapping(target = "job",         source = "role")
  @Mapping(target = "type",        constant = "MOVIE")
  PersonMovieCrewCredit toPersonMovieCrewCredit(MovieCredit credit);

  @Mapping(target = "creditId",     source = "id")
  @Mapping(target = "id",           source = "series.id")
  @Mapping(target = "title",        source = "series.title")
  @Mapping(target = "firstAirDate", source = "series.firstAirDate")
  @Mapping(target = "score",        source = "series.score")
  @Mapping(target = "backdrop",     source = "series.backdrop", qualifiedByName = "imageToString")
  @Mapping(target = "poster",       source = "series.poster",   qualifiedByName = "imageToString")
  @Mapping(target = "overview",     source = "series.overview")
  @Mapping(target = "jobs",         source = "roles")
  @Mapping(target = "type",         constant = "SERIES")
  PersonSeriesCrewCredit toPersonSeriesCrewCredit(SeriesCredit credit);

  @Mapping(target = "creditId",  source = "id")
  @Mapping(target = "character", source = "role")
  Role toRole(com.erdouglass.emdb.media.series.Role role);

  @Mapping(target = "creditId", source = "id")
  @Mapping(target = "title",    source = "role")
  Job toJob(com.erdouglass.emdb.media.series.Role role);
  
  @ObjectFactory
  default Person newPerson(SavePerson command) {
    return new Person(command.tmdbId());
  }
}
