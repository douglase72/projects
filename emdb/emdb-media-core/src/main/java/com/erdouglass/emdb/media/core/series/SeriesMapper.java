package com.erdouglass.emdb.media.core.series;

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
import com.erdouglass.emdb.media.series.SaveSeries;
import com.erdouglass.emdb.media.series.SeriesDto;
import com.erdouglass.emdb.media.series.SeriesDto.SeriesCastCredit;
import com.erdouglass.emdb.media.series.SeriesDto.SeriesCredits;
import com.erdouglass.emdb.media.series.SeriesDto.SeriesCrewCredit;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
interface SeriesMapper extends CommonMapper {

  @Mapping(target = "credits",       ignore = true)
  @Mapping(target = "firstAirDate",  ignore = true)
  @Mapping(source = "backdrop.name", target = "backdrop")
  @Mapping(source = "poster.name",   target = "poster")
  void merge(SaveSeries command, @MappingTarget Series series);
  
  @Mapping(target = "credits",       ignore = true)
  @Mapping(target = "firstAirDate",  ignore = true)
  @Mapping(source = "backdrop.name", target = "backdrop")
  @Mapping(source = "poster.name",   target = "poster")
  Series toSeries(SaveSeries command);
  
  @Mapping(target = "credits",     ignore = true)
  @Mapping(target = "lastAirDate", ignore = true)
  @Mapping(source = "backdrop",    target = "backdrop", qualifiedByName = "imageToString")
  @Mapping(source = "poster",      target = "poster",   qualifiedByName = "imageToString")
  SeriesDto toSeriesView(Series series);
  
  @Mapping(target = "lastAirDate", ignore = true)
  @Mapping(source = "backdrop",    target = "backdrop", qualifiedByName = "imageToString")
  @Mapping(source = "poster",      target = "poster",   qualifiedByName = "imageToString")
  SeriesDto toSeriesDto(Series series);
  
  @Mapping(target = "seriesCredit", ignore = true)
  @Mapping(source = "character",    target = "role")
  Role toRole(SaveSeries.CastCredit.Role role);
  
  @Mapping(target = "seriesCredit", ignore = true)
  @Mapping(source = "title",        target = "role")
  Role toRole(SaveSeries.CrewCredit.Job job);
  
  @Mapping(source = "id", target = "creditId")
  @Mapping(source = "role", target = "character")
  com.erdouglass.emdb.media.series.Role toCastRole(Role role);
  
  @Mapping(source = "id", target = "creditId")
  @Mapping(source = "role", target = "title")
  com.erdouglass.emdb.media.series.Job toCrewJob(Role role);
  
  @Mapping(source = "person.id",      target = "id")
  @Mapping(source = "person.name",    target = "name")
  @Mapping(source = "person.gender",  target = "gender")
  @Mapping(source = "person.profile", target = "profile", qualifiedByName = "imageToString")    
  SeriesCastCredit toCastCredit(SeriesCredit credit);
  
  @Mapping(source = "person.id",      target = "id")
  @Mapping(source = "person.name",    target = "name")
  @Mapping(source = "person.gender",  target = "gender")
  @Mapping(source = "person.profile", target = "profile", qualifiedByName = "imageToString")
  @Mapping(source = "roles",          target = "jobs")
  SeriesCrewCredit toCrewCredit(SeriesCredit credit);
  
  default SeriesCredits toCredits(List<SeriesCredit> credits) {
    if (credits == null) {
      return null;
    }
    var cast = new ArrayList<SeriesCastCredit>();
    var crew = new ArrayList<SeriesCrewCredit>();
    for (SeriesCredit credit : credits) {
      if (credit.getType() == CreditType.CAST) {
        cast.add(toCastCredit(credit));
      } else {
        crew.add(toCrewCredit(credit));
      }
    }
    return new SeriesCredits(cast, crew);
  }
  
  @ObjectFactory
  default Series createSeries(SaveSeries command) {
    return new Series(command.tmdbId());
  }
  
  @ObjectFactory
  default Role createRole(SaveSeries.CastCredit.Role role) {
    return new Role(role.creditId());
  }

  @ObjectFactory
  default Role createRole(SaveSeries.CrewCredit.Job job) {
    return new Role(job.creditId());
  }
}
