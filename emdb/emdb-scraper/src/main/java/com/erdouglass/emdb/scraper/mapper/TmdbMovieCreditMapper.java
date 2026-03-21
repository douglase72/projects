package com.erdouglass.emdb.scraper.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.comand.SaveMovie.CastCredit;
import com.erdouglass.emdb.common.comand.SaveMovie.CrewCredit;
import com.erdouglass.emdb.scraper.query.TmdbMovie;

@Mapper(
    componentModel = "cdi", 
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface TmdbMovieCreditMapper {
  
  @Mapping(source = "credit.id", target = "tmdbId")
  CastCredit toCastCredit(TmdbMovie.CastCredit credit);
  
  @Mapping(source = "credit.id", target = "tmdbId")
  CrewCredit toCrewCredit(TmdbMovie.CrewCredit credit);

}
