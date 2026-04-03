package com.erdouglass.emdb.media.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.comand.SaveSeries.CastCredit;
import com.erdouglass.emdb.common.comand.SaveSeries.CrewCredit;
import com.erdouglass.emdb.common.comand.UpdateRole;
import com.erdouglass.emdb.common.comand.UpdateSeriesCredit;
import com.erdouglass.emdb.common.query.CreditType;
import com.erdouglass.emdb.media.entity.Person;
import com.erdouglass.emdb.media.entity.Role;
import com.erdouglass.emdb.media.entity.Series;
import com.erdouglass.emdb.media.entity.SeriesCredit;
import com.erdouglass.emdb.media.proto.v1.UpdateRoleRequest;
import com.erdouglass.emdb.media.proto.v1.UpdateRoleResponse;
import com.erdouglass.emdb.media.proto.v1.UpdateSeriesCreditRequest;
import com.erdouglass.emdb.media.proto.v1.UpdateSeriesCreditResponse;

@Mapper(
    componentModel = "cdi", 
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface SeriesCreditMapper {
  
  void merge(SeriesCredit source, @MappingTarget SeriesCredit target);
  void merge(UpdateSeriesCredit command, @MappingTarget SeriesCredit credit);
  void merge(UpdateRole command, @MappingTarget Role role);

  default SeriesCredit toSeriesCredit(Series series, Person person, CastCredit credit) {
    var toReturn = new SeriesCredit();
    toReturn.setType(CreditType.CAST);
    toReturn.setSeries(series);
    toReturn.setPerson(person);
    toReturn.setRoles(credit.roles().stream().map(r -> Role.of(r.character(), r.episodeCount())).toList());
    toReturn.setOrder(credit.order());
    toReturn.setTotalEpisodes(credit.roles().stream().mapToInt(CastCredit.Role::episodeCount).sum());
    return toReturn;
  }
  
  default SeriesCredit toSeriesCredit(Series series, Person person, CrewCredit credit) {
    var toReturn = new SeriesCredit();
    toReturn.setType(CreditType.CREW);
    toReturn.setSeries(series);
    toReturn.setPerson(person);
    toReturn.setRoles(credit.jobs().stream().map(j -> Role.of(j.title(), j.episodeCount())).toList());
    toReturn.setTotalEpisodes(credit.jobs().stream().mapToInt(CrewCredit.Job::episodeCount).sum());
    return toReturn;
  }
  
  UpdateSeriesCredit toUpdateSeriesCredit(UpdateSeriesCreditRequest request);
  
  @Mapping(source = "id", target = "creditId")
  UpdateSeriesCreditResponse toUpdateSeriesCreditResponse(SeriesCredit credit);
  
  UpdateRole toUpdateRole(UpdateRoleRequest request);
  
  UpdateRoleResponse toUpdateRoleResponse(Role role);
  
}
