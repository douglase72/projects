package com.erdouglass.emdb.scraper.mapper;

import com.erdouglass.emdb.common.CreditType;
import com.erdouglass.emdb.common.command.MovieCreditCreateCommand;
import com.erdouglass.emdb.scraper.dto.TmdbMovie.CastCredit;
import com.erdouglass.emdb.scraper.dto.TmdbMovie.CrewCredit;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TmdbCreditMapper {
  
  public MovieCreditCreateCommand toCastCredit(CastCredit credit) {
    return MovieCreditCreateCommand.builder()
        .creditType(CreditType.CAST)
        .personId(credit.id())
        .role(credit.character())
        .order(credit.order())
        .build();
  }
  
  public MovieCreditCreateCommand toCrewCredit(CrewCredit credit) {
    return MovieCreditCreateCommand.builder()
        .creditType(CreditType.CAST)
        .personId(credit.id())
        .role(credit.job())
        .build();
  }

}
