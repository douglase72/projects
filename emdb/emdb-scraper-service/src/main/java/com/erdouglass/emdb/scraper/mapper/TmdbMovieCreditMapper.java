package com.erdouglass.emdb.scraper.mapper;

import jakarta.enterprise.context.ApplicationScoped;

import com.erdouglass.emdb.common.CreditType;
import com.erdouglass.emdb.common.message.MovieCreditCreateMessage;
import com.erdouglass.emdb.scraper.query.TmdbMovieDto.CastCredit;
import com.erdouglass.emdb.scraper.query.TmdbMovieDto.CrewCredit;

@ApplicationScoped
public class TmdbMovieCreditMapper {
  
  public MovieCreditCreateMessage toCastCredit(CastCredit credit) {
    return MovieCreditCreateMessage.builder()
        .tmdbId(credit.credit_id())
        .creditType(CreditType.CAST)
        .personId(credit.id())
        .role(credit.character())
        .order(credit.order())
        .build();
  }
  
  public MovieCreditCreateMessage toCrewCredit(CrewCredit credit) {
    return MovieCreditCreateMessage.builder()
        .tmdbId(credit.credit_id())
        .creditType(CreditType.CAST)
        .personId(credit.id())
        .role(credit.job())
        .build();
  }

}
