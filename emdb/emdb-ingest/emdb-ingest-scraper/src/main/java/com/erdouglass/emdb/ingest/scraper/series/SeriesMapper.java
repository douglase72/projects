package com.erdouglass.emdb.ingest.scraper.series;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.ingest.scraper.series.Series.TmdbCastCredit;
import com.erdouglass.emdb.ingest.scraper.series.Series.TmdbCrewCredit;
import com.erdouglass.emdb.media.Image;
import com.erdouglass.emdb.media.series.SaveSeries;
import com.erdouglass.emdb.media.series.SaveSeries.CastCredit;
import com.erdouglass.emdb.media.series.SaveSeries.CrewCredit;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
interface SeriesMapper {

  @BeanMapping(builder = @Builder(disableBuilder = true))
  @Mapping(source = "series.id", target = "tmdbId")
  @Mapping(source = "series.name", target = "title")
  @Mapping(source = "series.vote_average", target = "score")
  @Mapping(source = "series.original_language", target = "originalLanguage")
  @Mapping(source = "series.aggregate_credits", target = "credits")
  SaveSeries toSaveSeries(Series series, Image backdrop, Image poster);
  
  @Mapping(source = "id", target = "tmdbId")
  @Mapping(source = "profile_path", target = "profile")
  CastCredit toCastCredit(TmdbCastCredit credit);
  
  @Mapping(source = "id", target = "tmdbId")
  @Mapping(source = "profile_path", target = "profile")
  CrewCredit toCastCredit(TmdbCrewCredit credit);
  
  @Mapping(source = "credit_id", target = "creditId")
  @Mapping(source = "role.episode_count", target = "episodeCount")
  CastCredit.Role toRole(TmdbCastCredit.Role role);
  
  @Mapping(source = "credit_id", target = "creditId")
  @Mapping(source = "job.job", target = "title")
  @Mapping(source = "job.episode_count", target = "episodeCount")
  CrewCredit.Job toJob(TmdbCrewCredit.Job job);
}
