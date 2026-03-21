package com.erdouglass.emdb.scraper.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.comand.SaveSeries.CastCredit;
import com.erdouglass.emdb.common.comand.SaveSeries.CrewCredit;
import com.erdouglass.emdb.scraper.query.TmdbSeries;

@Mapper(
    componentModel = "cdi", 
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface TmdbSeriesCreditMapper {
  
  @Mapping(source = "role.episode_count", target = "episodeCount")
  CastCredit.Role toRole(TmdbSeries.CastCredit.Role role);

  @Mapping(source = "credit.id", target = "tmdbId")
  CastCredit toCastCredit(TmdbSeries.CastCredit credit);
  
  @Mapping(source = "job.job", target = "title")
  @Mapping(source = "job.episode_count", target = "episodeCount")
  CrewCredit.Job toJob(TmdbSeries.CrewCredit.Job job);
  
  @Mapping(source = "credit.id", target = "tmdbId")
  CrewCredit toCrewCredit(TmdbSeries.CrewCredit credit);
  
}
