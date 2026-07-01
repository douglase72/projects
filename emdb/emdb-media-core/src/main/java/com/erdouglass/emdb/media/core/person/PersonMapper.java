package com.erdouglass.emdb.media.core.person;

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

import com.erdouglass.emdb.media.core.CommonMapper;
import com.erdouglass.emdb.media.core.credit.Credit;
import com.erdouglass.emdb.media.core.movie.MovieCredit;
import com.erdouglass.emdb.media.person.PersonDto;
import com.erdouglass.emdb.media.person.PersonDto.PersonCastCredit;
import com.erdouglass.emdb.media.person.PersonDto.PersonCredits;
import com.erdouglass.emdb.media.person.PersonDto.PersonCrewCredit;
import com.erdouglass.emdb.media.person.PersonDto.PersonMovieCastCredit;
import com.erdouglass.emdb.media.person.PersonDto.PersonMovieCrewCredit;
import com.erdouglass.emdb.media.person.SavePerson;

@Mapper(
    componentModel = "cdi",
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION
)
interface PersonMapper extends CommonMapper {

  @Mapping(target = "credits",      ignore = true)
  @Mapping(source = "profile.name", target = "profile")
  void merge(SavePerson command, @MappingTarget Person person);
  
  @Mapping(target = "credits",      ignore = true)
  @Mapping(source = "profile.name", target = "profile")
  Person toPerson(SavePerson command);
  
  @Mapping(target = "credits", ignore = true)
  @Mapping(source = "profile", target = "profile", qualifiedByName = "imageToString")
  PersonDto toPersonDto(Person person);
  
  @SubclassMapping(source = MovieCredit.class,  target = PersonMovieCastCredit.class)
  PersonCastCredit toCastCredit(Credit credit);

  @SubclassMapping(source = MovieCredit.class,  target = PersonMovieCrewCredit.class)
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
  
  default PersonCredits toCredits(List<Credit> credits) {
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
    return new PersonCredits(cast, crew);
  }
  
  @ObjectFactory
  default Person createPerson(SavePerson command) {
    return new Person(command.tmdbId());
  }
}
