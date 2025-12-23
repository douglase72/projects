package com.erdouglass.emdb.media.mapper;

import jakarta.enterprise.context.ApplicationScoped;

import com.erdouglass.emdb.common.CreditType;
import com.erdouglass.emdb.common.query.MovieCreditDto;
import com.erdouglass.emdb.media.entity.MovieCredit;

@ApplicationScoped
public class MovieCreditMapper {

  public MovieCreditDto toMovieCreditDto(MovieCredit credit) {
    var mcb = MovieCreditDto.builder()
        .creditId(credit.id())
        .id(credit.person().id())
        .name(credit.person().name())
        .gender(credit.person().gender())
        .profile(credit.person().profile().orElse(null))
        .order(credit.order().orElse(null));
    if (credit.type() == CreditType.CAST) {
      mcb.character(credit.role());
    } else {
      mcb.job(credit.role());
    }
    return mcb.build();
  }
  
}