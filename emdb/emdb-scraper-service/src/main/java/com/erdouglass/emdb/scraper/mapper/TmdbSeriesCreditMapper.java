package com.erdouglass.emdb.scraper.mapper;

import com.erdouglass.emdb.common.CreditType;
import com.erdouglass.emdb.common.command.SeriesCreditCreateCommand;
import com.erdouglass.emdb.common.command.SeriesCreditCreateCommand.Role;
import com.erdouglass.emdb.scraper.dto.TmdbSeries.CastCredit;
import com.erdouglass.emdb.scraper.dto.TmdbSeries.CrewCredit;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TmdbSeriesCreditMapper {
  
  public SeriesCreditCreateCommand toCastCredit(CastCredit credit) {
    return SeriesCreditCreateCommand.builder()
        .creditType(CreditType.CAST)
        .personId(credit.id())
        .roles(credit.roles().stream()
            .map(r -> Role.of(r.credit_id(), r.character(), r.episode_count()))
            .toList())
        .order(credit.order())
        .build();
  }
  
  public SeriesCreditCreateCommand toCrewCredit(CrewCredit credit) {
    return SeriesCreditCreateCommand.builder()
        .creditType(CreditType.CAST)
        .personId(credit.id())
        .roles(credit.jobs().stream()
            .map(j -> Role.of(j.credit_id(), j.job(), j.episode_count()))
            .toList())        
        .build();
  }

}
