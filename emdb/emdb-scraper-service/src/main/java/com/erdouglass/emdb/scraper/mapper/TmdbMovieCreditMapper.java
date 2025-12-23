package com.erdouglass.emdb.scraper.mapper;

import jakarta.enterprise.context.ApplicationScoped;

import com.erdouglass.emdb.common.CreditType;
import com.erdouglass.emdb.common.MovieCreditCreateDto;
import com.erdouglass.emdb.scraper.query.TmdbMovieDto.CastCredit;
import com.erdouglass.emdb.scraper.query.TmdbMovieDto.CrewCredit;

@ApplicationScoped
public class TmdbMovieCreditMapper {
  
  public MovieCreditCreateDto toCastCredit(CastCredit credit) {
    return MovieCreditCreateDto.builder()
        .tmdbId(credit.credit_id())
        .creditType(CreditType.CAST)
        .personId(credit.id())
        .role(credit.character())
        .order(credit.order())
        .build();
  }
  
  public MovieCreditCreateDto toCrewCredit(CrewCredit credit) {
    return MovieCreditCreateDto.builder()
        .tmdbId(credit.credit_id())
        .creditType(CreditType.CAST)
        .personId(credit.id())
        .role(credit.job())
        .build();
  }

}
