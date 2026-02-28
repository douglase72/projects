package com.erdouglass.emdb.media.mapper;

import jakarta.enterprise.context.ApplicationScoped;

import com.erdouglass.emdb.common.CreditType;
import com.erdouglass.emdb.common.MediaType;
import com.erdouglass.emdb.common.query.JobDto;
import com.erdouglass.emdb.common.query.PersonCreditDto;
import com.erdouglass.emdb.common.query.PersonMovieCreditDto;
import com.erdouglass.emdb.common.query.PersonSeriesCreditDto;
import com.erdouglass.emdb.common.query.RoleDto;
import com.erdouglass.emdb.media.entity.Credit;
import com.erdouglass.emdb.media.entity.MovieCredit;
import com.erdouglass.emdb.media.entity.SeriesCredit;

@ApplicationScoped
public class PersonCreditMapper {
  
  public PersonCreditDto toPersonCreditDto(Credit credit) {
    return switch (credit) {
      case MovieCredit c -> {
        var movie = c.movie();
        var pcb = PersonMovieCreditDto.builder()
            .creditId(c.id())
            .id(movie.id())
            .title(movie.title())
            .releaseDate(movie.releaseDate().orElse(null))
            .score(movie.score().orElse(null))
            .backdrop(movie.backdrop().map(b -> String.format("%s.jpg", b)).orElse(null))
            .poster(movie.poster().map(p -> String.format("%s.jpg", p)).orElse(null))
            .overview(movie.overview().orElse(null))
            .type(MediaType.MOVIE);
        if (c.type() == CreditType.CAST) {
          pcb.character(c.role());
        } else {
          pcb.job(c.role());
        }
        yield pcb.build();
      }
      case SeriesCredit c -> {
        var series = c.series();
        var pcb = PersonSeriesCreditDto.builder()
            .creditId(c.id())
            .id(series.id())
            .title(series.title())
            .score(series.score().orElse(null))
            .backdrop(series.backdrop().map(b -> String.format("%s.jpg", b)).orElse(null))
            .poster(series.poster().map(p -> String.format("%s.jpg", p)).orElse(null))
            .overview(series.overview().orElse(null))
            .type(MediaType.SERIES);
        if (c.type() == CreditType.CAST) {
          pcb.roles(c.roles().stream().map(r -> RoleDto.of(r.id(), r.role(), r.episodeCount())).toList());
        } else {
          pcb.jobs(c.roles().stream().map(r -> JobDto.of(r.id(), r.role(), r.episodeCount())).toList());
        }        
        yield pcb.build();
      }
      default -> throw new IllegalArgumentException("Invalid credit: " + credit);
    };
  }

}