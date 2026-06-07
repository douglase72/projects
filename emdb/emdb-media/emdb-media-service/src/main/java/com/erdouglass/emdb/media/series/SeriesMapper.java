package com.erdouglass.emdb.media.series;

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
import com.erdouglass.emdb.media.command.SaveSeries;
import com.erdouglass.emdb.media.internal.CommonMapper;
import com.erdouglass.emdb.media.query.SeriesResponse;
import com.erdouglass.emdb.media.query.SeriesResponse.CastCredit;
import com.erdouglass.emdb.media.query.SeriesResponse.Credits;
import com.erdouglass.emdb.media.query.SeriesResponse.CrewCredit;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
interface SeriesMapper extends CommonMapper {
  
  @Mapping(target = "credits", ignore = true)
  @Mapping(target = "firstAirDate", ignore = true)
  @Mapping(source = "command.backdrop.tmdbName", target = "tmdbBackdrop")
  @Mapping(source = "command.backdrop.emdbName", target = "backdrop")
  @Mapping(source = "command.poster.tmdbName",   target = "tmdbPoster")
  @Mapping(source = "command.poster.emdbName",   target = "poster")
  void merge(SaveSeries command, @MappingTarget Series series);

  @Mapping(target = "credits", ignore = true)
  @Mapping(target = "firstAirDate", ignore = true)
  @Mapping(source = "backdrop.tmdbName", target = "tmdbBackdrop")
  @Mapping(source = "backdrop.emdbName", target = "backdrop")
  @Mapping(source = "poster.tmdbName",   target = "tmdbPoster")
  @Mapping(source = "poster.emdbName",   target = "poster")
  Series toSeries(SaveSeries command, Image backdrop, Image poster);
  
  @Mapping(target = "lastAirDate", ignore = true)
  SeriesResponse toSeriesResponse(Series series);
  
  @Mapping(target = "credits", ignore = true)
  @Mapping(target = "lastAirDate", ignore = true)
  @Mapping(source = "backdrop", target = "backdrop", qualifiedByName = "imageToString")
  @Mapping(source = "poster",   target = "poster",   qualifiedByName = "imageToString")
  SeriesResponse toSeriesView(Series series);
  
  @Mapping(target = "seriesCredit", ignore = true)
  @Mapping(source = "character", target = "role")
  Role toRole(SaveSeries.CastCredit.Role role);
  
  @Mapping(target = "seriesCredit", ignore = true)
  @Mapping(source = "title", target = "role")
  Role toRole(SaveSeries.CrewCredit.Job job);
  
  default Credits toCredits(List<SeriesCredit> credits) {
    if (credits == null) {
      return null;
    }
    var cast = new ArrayList<CastCredit>();
    var crew = new ArrayList<CrewCredit>();
    for (SeriesCredit credit : credits) {
      switch (credit.getType()) {
        case CAST -> cast.add(toCastCredit(credit));
        case CREW -> crew.add(toCrewCredit(credit));
      }
    }
    return new Credits(cast, crew);
  }
  
  @Mapping(source = "person.id",      target = "id")
  @Mapping(source = "person.name",    target = "name")
  @Mapping(source = "person.gender",  target = "gender")
  @Mapping(source = "person.profile", target = "profile", qualifiedByName = "imageToString")    
  CastCredit toCastCredit(SeriesCredit credit);
  
  @Mapping(source = "person.id",      target = "id")
  @Mapping(source = "person.name",    target = "name")
  @Mapping(source = "person.gender",  target = "gender")
  @Mapping(source = "person.profile", target = "profile", qualifiedByName = "imageToString")
  @Mapping(source = "roles",          target = "jobs")
  CrewCredit toCrewCredit(SeriesCredit credit);
  
  @Mapping(source = "id", target = "creditId")
  @Mapping(source = "role", target = "character")
  com.erdouglass.emdb.media.query.Role toCastRole(Role role);
  
  @Mapping(source = "id", target = "creditId")
  @Mapping(source = "role", target = "title")
  com.erdouglass.emdb.media.query.Job toCrewJob(Role role);
  
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
