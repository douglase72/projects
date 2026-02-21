package com.erdouglass.emdb.media.mapper;

import jakarta.enterprise.context.ApplicationScoped;

import com.erdouglass.emdb.common.CreditType;
import com.erdouglass.emdb.common.query.PersonCreditDto;
import com.erdouglass.emdb.media.entity.Credit;
import com.erdouglass.emdb.media.entity.MovieCredit;

@ApplicationScoped
public class PersonCreditMapper {
  
  public PersonCreditDto toPersonCreditDto(Credit credit) {
    return switch (credit) {
      case MovieCredit c -> {
        var movie = c.movie();
        var pcb = PersonCreditDto.builder()
            .creditId(c.id())
            .id(movie.id())
            .title(movie.title())
            .releaseDate(movie.releaseDate().orElse(null))
            .score(movie.score().orElse(null))
            .backdrop(movie.backdrop().map(b -> String.format("%s.jpg", b)).orElse(null))
            .poster(movie.poster().map(p -> String.format("%s.jpg", p)).orElse(null))
            .overview(movie.overview().orElse(null));
        if (c.type() == CreditType.CAST) {
          pcb.character(c.role());
        } else {
          pcb.job(c.role());
        }
        yield pcb.build();
      }
      default -> throw new IllegalArgumentException("Invalid credit: " + credit);
    };
  }

}