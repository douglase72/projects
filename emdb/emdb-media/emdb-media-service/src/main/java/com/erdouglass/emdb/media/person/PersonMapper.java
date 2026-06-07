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

import com.erdouglass.emdb.media.Image;
import com.erdouglass.emdb.media.command.SavePerson;
import com.erdouglass.emdb.media.command.SaveSeries;
import com.erdouglass.emdb.media.credit.Credit;
import com.erdouglass.emdb.media.credit.CreditType;
import com.erdouglass.emdb.media.internal.CommonMapper;
import com.erdouglass.emdb.media.movie.MovieCredit;
import com.erdouglass.emdb.media.query.PersonResponse;
import com.erdouglass.emdb.media.query.PersonResponse.CastCredit;
import com.erdouglass.emdb.media.query.PersonResponse.Credits;
import com.erdouglass.emdb.media.query.PersonResponse.CrewCredit;
import com.erdouglass.emdb.media.series.Role;
import com.erdouglass.emdb.media.series.SeriesCredit;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
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
    var cast = new ArrayList<CastCredit>();
    var crew = new ArrayList<CrewCredit>();
    for (var credit : credits) {
      if (credit.getType() == CreditType.CAST) {
        cast.add(toCastCredit(credit));
      } else {
        crew.add(toCrewCredit(credit));
      }
    }
    return new Credits(cast, crew);
  }

  default CastCredit toCastCredit(Credit credit) {
    return switch (credit) {
      case MovieCredit m  -> movieCast(m);
      case SeriesCredit s -> seriesCast(s);
      default -> throw new IllegalArgumentException("Unexpected credit: " + credit);
    };
  }

  default CrewCredit toCrewCredit(Credit credit) {
    return switch (credit) {
      case MovieCredit m  -> movieCrew(m);
      case SeriesCredit s -> seriesCrew(s);
      default -> throw new IllegalArgumentException("Unexpected credit: " + credit);
    };
  }
  
  @Mapping(source = "id", target = "creditId")
  @Mapping(source = "movie.id", target = "id")
  @Mapping(source = "movie.title", target = "title")
  @Mapping(source = "movie.score", target = "score")
  @Mapping(source = "movie.backdrop", target = "backdrop", qualifiedByName = "imageToString")
  @Mapping(source = "movie.poster", target = "poster", qualifiedByName = "imageToString")
  @Mapping(source = "movie.overview", target = "overview")
  @Mapping(source = "movie.releaseDate", target = "releaseDate")
  @Mapping(source = "role", target = "character")
  @Mapping(target = "type", constant = "MOVIE")
  @Mapping(target = "firstAirDate", ignore = true)
  @Mapping(target = "roles", ignore = true)
  CastCredit movieCast(MovieCredit credit);

  @Mapping(source = "id", target = "creditId")
  @Mapping(source = "series.id", target = "id")
  @Mapping(source = "series.title", target = "title")
  @Mapping(source = "series.score", target = "score")
  @Mapping(source = "series.backdrop", target = "backdrop", qualifiedByName = "imageToString")
  @Mapping(source = "series.poster", target = "poster", qualifiedByName = "imageToString")
  @Mapping(source = "series.overview", target = "overview")
  @Mapping(source = "series.firstAirDate", target = "firstAirDate")
  @Mapping(source = "roles", target = "roles")
  @Mapping(target = "type", constant = "SERIES")
  @Mapping(target = "releaseDate", ignore = true)
  @Mapping(target = "character", ignore = true)
  CastCredit seriesCast(SeriesCredit credit);
  
  @Mapping(source = "id", target = "creditId")
  @Mapping(source = "movie.id", target = "id")
  @Mapping(source = "movie.title", target = "title")
  @Mapping(source = "movie.score", target = "score")
  @Mapping(source = "movie.backdrop", target = "backdrop", qualifiedByName = "imageToString")
  @Mapping(source = "movie.poster", target = "poster", qualifiedByName = "imageToString")
  @Mapping(source = "movie.overview", target = "overview")
  @Mapping(source = "movie.releaseDate", target = "releaseDate")
  @Mapping(source = "role", target = "job")
  @Mapping(target = "type", constant = "MOVIE")
  @Mapping(target = "firstAirDate", ignore = true)
  @Mapping(target = "jobs", ignore = true)
  CrewCredit movieCrew(MovieCredit credit);

  @Mapping(source = "id", target = "creditId")
  @Mapping(source = "series.id", target = "id")
  @Mapping(source = "series.title", target = "title")
  @Mapping(source = "series.score", target = "score")
  @Mapping(source = "series.backdrop", target = "backdrop", qualifiedByName = "imageToString")
  @Mapping(source = "series.poster", target = "poster", qualifiedByName = "imageToString")
  @Mapping(source = "series.overview", target = "overview")
  @Mapping(source = "series.firstAirDate", target = "firstAirDate")
  @Mapping(source = "roles", target = "jobs")
  @Mapping(target = "type", constant = "SERIES")
  @Mapping(target = "releaseDate", ignore = true)
  @Mapping(target = "job", ignore = true)
  CrewCredit seriesCrew(SeriesCredit credit);
  
  @Mapping(source = "id",   target = "creditId")
  @Mapping(source = "role", target = "character")
  com.erdouglass.emdb.media.query.Role toRoleDto(com.erdouglass.emdb.media.series.Role role);

  @Mapping(source = "id",   target = "creditId")
  @Mapping(source = "role", target = "title")
  com.erdouglass.emdb.media.query.Job toJobDto(com.erdouglass.emdb.media.series.Role role);
  
  @ObjectFactory
  default Role createRole(SaveSeries.CastCredit.Role role) { 
    return new Role(role.creditId()); 
  }

  @ObjectFactory
  default Role createRole(SaveSeries.CrewCredit.Job job) { 
    return new Role(job.creditId()); 
  }
  
  @ObjectFactory
  default Person createPerson(SavePerson command) {
    return new Person(command.tmdbId());
  }
}