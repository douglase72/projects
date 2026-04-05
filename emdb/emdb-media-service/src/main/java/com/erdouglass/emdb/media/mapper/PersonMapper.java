package com.erdouglass.emdb.media.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.common.comand.UpdatePerson;
import com.erdouglass.emdb.common.query.CreditType;
import com.erdouglass.emdb.media.dto.SaveResult;
import com.erdouglass.emdb.media.entity.Credit;
import com.erdouglass.emdb.media.entity.MovieCredit;
import com.erdouglass.emdb.media.entity.Person;
import com.erdouglass.emdb.media.entity.Role;
import com.erdouglass.emdb.media.entity.SeriesCredit;
import com.erdouglass.emdb.media.proto.v1.CastRoles;
import com.erdouglass.emdb.media.proto.v1.CreditResponse;
import com.erdouglass.emdb.media.proto.v1.CrewJobs;
import com.erdouglass.emdb.media.proto.v1.MovieCreditDetails;
import com.erdouglass.emdb.media.proto.v1.PeopleResponse;
import com.erdouglass.emdb.media.proto.v1.PersonResponse;
import com.erdouglass.emdb.media.proto.v1.SavePersonRequest;
import com.erdouglass.emdb.media.proto.v1.SavePersonResponse;
import com.erdouglass.emdb.media.proto.v1.SeriesCreditDetails;
import com.erdouglass.emdb.media.proto.v1.UpdatePersonCommand;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface PersonMapper {
  
  @Mapping(source = "profile.name", target = "profile")
  @Mapping(source = "profile.tmdbName", target = "tmdbProfile")
  void merge(SavePerson command, @MappingTarget Person person);
  
  void merge(UpdatePerson command, @MappingTarget Person person);
  
  @BeanMapping(builder = @Builder(disableBuilder = true))
  SavePerson toSavePerson(SavePersonRequest request);
  
  @Mapping(target = "profile", expression = "java(person.getProfile() != null ? com.erdouglass.emdb.common.comand.Image.of(person.getProfile(), person.getTmdbProfile()) : null)")
  SavePerson toSavePerson(Person person);
  
  @Mapping(source = "profile.name", target = "profile")
  @Mapping(source = "profile.tmdbName", target = "tmdbProfile")
  Person toPerson(SavePerson command);
  
  @BeanMapping(builder = @Builder(disableBuilder = true))
  UpdatePerson toUpdatePerson(UpdatePersonCommand command);
  
  @Mapping(source = "entity", target = "person")
  SavePersonResponse toSavePersonResponse(SaveResult<Person> result);
  
  /// These methods are for batch processing
  @BeanMapping(builder = @Builder(disableBuilder = true))
  List<SavePerson> toSavePeople(List<SavePersonRequest> requests);
  
  @Mapping(source = "entity.id", target = "id")
  @Mapping(source = "entity.tmdbId", target = "tmdbId")
  com.erdouglass.emdb.media.proto.v1.SaveResult toSavePersonResult(SaveResult<Person> result);
  
  com.erdouglass.emdb.media.proto.v1.CreditType toCreditType(CreditType type);
  
  @Mapping(source = "role", target = "character")
  com.erdouglass.emdb.media.proto.v1.Role toRole(Role role);
  
  @Mapping(source = "role", target = "title")
  com.erdouglass.emdb.media.proto.v1.Job toJob(Role role);
  
  default MovieCreditDetails toMovieCreditDetails(MovieCredit mc) {
    var movie = mc.getMovie();
    var builder = MovieCreditDetails.newBuilder()
        .setCreditId(mc.getId().toString())
        .setId(movie.getId())
        .setTitle(movie.getTitle());
    if (movie.getReleaseDate() != null) builder.setReleaseDate(movie.getReleaseDate().toString());
    if (movie.getScore() != null)       builder.setScore(movie.getScore());
    if (movie.getBackdrop() != null)    builder.setBackdrop(movie.getBackdrop().toString());
    if (movie.getPoster() != null)      builder.setPoster(movie.getPoster().toString());
    if (movie.getOverview() != null)    builder.setOverview(movie.getOverview());
    if (mc.getType() == CreditType.CAST && mc.getRole() != null) {
      builder.setCharacter(mc.getRole());
    } else if (mc.getType() == CreditType.CREW && mc.getRole() != null) {
      builder.setJob(mc.getRole());
    }
    return builder.build();
  }
  
  default SeriesCreditDetails toSeriesCreditDetails(SeriesCredit sc) {
    var series = sc.getSeries();
    var builder = SeriesCreditDetails.newBuilder()
        .setCreditId(sc.getId().toString())
        .setId(series.getId())
        .setTitle(series.getTitle());
    if (series.getFirstAirDate() != null) builder.setFirstAirDate(series.getFirstAirDate().toString());
    if (series.getScore() != null)        builder.setScore(series.getScore());
    if (series.getBackdrop() != null)    builder.setBackdrop(series.getBackdrop().toString());
    if (series.getPoster() != null)      builder.setPoster(series.getPoster().toString());
    if (series.getOverview() != null)     builder.setOverview(series.getOverview());
    if (sc.getType() == CreditType.CAST) {
      var castRoles = CastRoles.newBuilder();
      sc.getRoles().stream().map(this::toRole).forEach(castRoles::addRoles);
      builder.setCastRoles(castRoles);
    } else if (sc.getType() == CreditType.CREW) {
      var crewJobs = CrewJobs.newBuilder();
      sc.getRoles().stream().map(this::toJob).forEach(crewJobs::addJobs);
      builder.setCrewJobs(crewJobs);
    }
    return builder.build();    
  }
  
  default CreditResponse toCreditResponse(Credit credit) {
    var builder = CreditResponse.newBuilder()
        .setId(credit.getId().toString())
        .setType(toCreditType(credit.getType()));
    if (credit.getOrder() != null) builder.setOrder(credit.getOrder());
    switch (credit) {
      case MovieCredit mc -> builder.setMovieCredit(toMovieCreditDetails(mc));
      case SeriesCredit sc -> builder.setSeriesCredit(toSeriesCreditDetails(sc));
      default -> throw new IllegalArgumentException("Invalid credit type");
    }
    return builder.build();
  }
  
  PersonResponse toPersonResponse(Person person);
  
  /// MapStruct cant convert a List into a protobuf wrapper message
  default PeopleResponse toPeopleResponse(List<SaveResult<Person>> results) {
    if (results == null) return null;
    var builder = PeopleResponse.newBuilder();
    results.stream()
        .map(this::toSavePersonResult)
        .forEach(builder::addResults);
    return builder.build();
  }
    
}
