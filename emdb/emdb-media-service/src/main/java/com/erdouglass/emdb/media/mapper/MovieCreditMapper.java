package com.erdouglass.emdb.media.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.common.CreditType;
import com.erdouglass.emdb.common.comand.SaveMovieCastCredit;
import com.erdouglass.emdb.common.comand.SaveMovieCrewCredit;
import com.erdouglass.emdb.common.query.MovieCastCreditDto;
import com.erdouglass.emdb.common.query.MovieCrewCreditDto;
import com.erdouglass.emdb.media.entity.Movie;
import com.erdouglass.emdb.media.entity.MovieCredit;
import com.erdouglass.emdb.media.entity.Person;

@ApplicationScoped
public class MovieCreditMapper {
  
  @Inject
  PersonMapper mapper;
  
  public MovieCredit toMovieCredit(SaveMovieCastCredit command, Movie movie, Person person) {
    var credit = new MovieCredit();
    credit.movie(movie);
    credit.person(person);
    credit.type(CreditType.CAST);
    credit.role(command.character());
    credit.order(command.order());
    return credit;
  }
  
  public MovieCredit toMovieCredit(SaveMovieCrewCredit command, Movie movie, Person person) {
    var credit = new MovieCredit();
    credit.movie(movie);
    credit.person(person);
    credit.type(CreditType.CREW);
    credit.role(command.job());
    return credit;
  }

  public MovieCastCreditDto toMovieCastCreditDto(MovieCredit credit) {
    return MovieCastCreditDto.builder()
        .creditId(credit.id())
        .id(credit.person().id())
        .name(credit.person().name())
        .gender(credit.person().gender().orElse(null))
        .profile(credit.person().profile().map(p -> String.format("%s.jpg", p)).orElse(null))
        .character(credit.role())
        .order(credit.order().orElse(null))
        .build();
  }
  
  public MovieCrewCreditDto toMovieCrewCreditDto(MovieCredit credit) {
    return MovieCrewCreditDto.builder()
        .creditId(credit.id())
        .id(credit.person().id())
        .name(credit.person().name())
        .gender(credit.person().gender().orElse(null))
        .profile(credit.person().profile().map(p -> String.format("%s.jpg", p)).orElse(null))
        .job(credit.role())
        .order(credit.order().orElse(null))
        .build();
  }  
  
}