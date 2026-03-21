package com.erdouglass.emdb.scraper.mapper;

import java.util.List;
import java.util.UUID;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.common.comand.SaveSeries;
import com.erdouglass.emdb.common.comand.SaveSeries.CastCredit;
import com.erdouglass.emdb.common.comand.SaveSeries.Credits;
import com.erdouglass.emdb.common.comand.SaveSeries.CrewCredit;
import com.erdouglass.emdb.scraper.query.TmdbSeries;

@Mapper(
    componentModel = "cdi", 
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    builder = @Builder(disableBuilder = true)
)
public interface TmdbSeriesMapper {
  
  @Mapping(source = "role.episode_count", target = "episodeCount")
  CastCredit.Role toRole(TmdbSeries.CastCredit.Role role);
  
  @Mapping(source = "job.job", target = "title")
  @Mapping(source = "job.episode_count", target = "episodeCount")
  CrewCredit.Job toJob(TmdbSeries.CrewCredit.Job job);

  @Mapping(source = "id", target = "tmdbId")
  CastCredit toCastCredit(TmdbSeries.CastCredit credit);

  @Mapping(source = "id", target = "tmdbId")
  CrewCredit toCrewCredit(TmdbSeries.CrewCredit credit);
  
  @Mapping(source = "series.id", target = "tmdbId")
  @Mapping(source = "series.name", target = "title")
  @Mapping(source = "series.vote_average", target = "score")
  @Mapping(source = "series.original_language", target = "originalLanguage")
  @Mapping(source = "backdrop", target = "backdrop")
  @Mapping(source = "poster", target = "poster")
  @Mapping(source = "credits", target = "credits")
  SaveSeries toSaveSeries(TmdbSeries series, UUID backdrop, UUID poster, Credits credits, List<SavePerson> people);
  
}
