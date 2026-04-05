package com.erdouglass.emdb.gateway.mapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.MediaType;
import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.common.comand.UpdatePerson;
import com.erdouglass.emdb.common.query.Job;
import com.erdouglass.emdb.common.query.MultiResponseDto;
import com.erdouglass.emdb.common.query.PersonCredit;
import com.erdouglass.emdb.common.query.PersonDto;
import com.erdouglass.emdb.common.query.PersonDto.Credits;
import com.erdouglass.emdb.common.query.PersonDto.MovieCredit;
import com.erdouglass.emdb.common.query.PersonDto.SeriesCredit;
import com.erdouglass.emdb.common.query.Role;
import com.erdouglass.emdb.media.proto.v1.CreditResponse;
import com.erdouglass.emdb.media.proto.v1.FindPersonRequest;
import com.erdouglass.emdb.media.proto.v1.MovieCreditDetails;
import com.erdouglass.emdb.media.proto.v1.PersonResponse;
import com.erdouglass.emdb.media.proto.v1.SavePeopleRequest;
import com.erdouglass.emdb.media.proto.v1.SavePersonRequest;
import com.erdouglass.emdb.media.proto.v1.SaveResult;
import com.erdouglass.emdb.media.proto.v1.SeriesCreditDetails;
import com.erdouglass.emdb.media.proto.v1.UpdatePersonRequest;

@Mapper(
    componentModel = "cdi", 
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS 
)
public interface PersonMapper extends CommonMapper {

  SavePersonRequest toSavePersonRequest(SavePerson command); 
  
  FindPersonRequest toFindPersonRequest(Long id, String append);
  
  @Mapping(target = "id", source = "id")
  @Mapping(target = "command", source = "command")
  UpdatePersonRequest toUpdatePersonRequest(Long id, UpdatePerson command);
  
  default SavePeopleRequest toSavePeopleRequest(List<SavePerson> commands) {
    if (commands == null) return null;
    return SavePeopleRequest.newBuilder()
        .addAllPeople(commands.stream().map(this::toSavePersonRequest).toList())
        .build();    
  }
  
  @Mapping(source = "status", target = "statusCode")
  MultiResponseDto toSaveResultDto(SaveResult result);
  
  List<MultiResponseDto> toMultiResponseDto(List<SaveResult> results);
  
  default MovieCredit toMovieCredit(MovieCreditDetails details) {
    return new PersonDto.MovieCredit(
        UUID.fromString(details.getCreditId()),
        details.getId(),
        details.getTitle(),
        details.hasReleaseDate() ? LocalDate.parse(details.getReleaseDate()) : null,
        details.hasScore() ? details.getScore() : null,
        details.hasBackdrop() ? details.getBackdrop() + ".jpg" : null,
        details.hasPoster() ? details.getPoster() + ".jpg" : null,
        details.hasOverview() ? details.getOverview() : null,
        details.hasCharacter() ? details.getCharacter() : null,
        details.hasJob() ? details.getJob() : null,
        MediaType.MOVIE
    );    
  }
  
  Role toRole(com.erdouglass.emdb.media.proto.v1.Role role);
  Job toJob(com.erdouglass.emdb.media.proto.v1.Job job);
  
  default SeriesCredit toSeriesCredit(SeriesCreditDetails details) {
    return new PersonDto.SeriesCredit(
        UUID.fromString(details.getCreditId()),
        details.getId(),
        details.getTitle(),
        details.hasFirstAirDate() ? LocalDate.parse(details.getFirstAirDate()) : null,
        details.hasScore() ? details.getScore() : null,
        details.hasBackdrop() ? details.getBackdrop() + ".jpg" : null,
        details.hasPoster() ? details.getPoster() + ".jpg" : null,
        details.hasOverview() ? details.getOverview() : null,
        details.hasCastRoles()
            ? details.getCastRoles().getRolesList().stream().map(this::toRole).toList()
            : List.of(),
        details.hasCrewJobs()
            ? details.getCrewJobs().getJobsList().stream().map(this::toJob).toList()
            : List.of(),
        MediaType.SERIES
    );
  }
  
  default Credits toCredits(List<CreditResponse> responses) {
    if (responses == null || responses.isEmpty()) return null;
    var cast = new ArrayList<PersonCredit>();
    var crew = new ArrayList<PersonCredit>();
    for (var response : responses) {
      PersonCredit credit = switch (response.getCreditDetailsCase()) {
        case MOVIE_CREDIT  -> toMovieCredit(response.getMovieCredit());
        case SERIES_CREDIT -> toSeriesCredit(response.getSeriesCredit());
        default -> null;
      };
      if (credit == null) continue;

      switch (response.getType()) {
        case CAST -> cast.add(credit);
        case CREW -> crew.add(credit);
        default -> { }
      }
    }
    return new Credits(List.copyOf(cast), List.copyOf(crew));
  }
  
  @Mapping(target = "profile", expression = "java(response.hasProfile() ? response.getProfile() + \".jpg\" : null)")
  PersonDto toPersonDto(PersonResponse response);
  
}