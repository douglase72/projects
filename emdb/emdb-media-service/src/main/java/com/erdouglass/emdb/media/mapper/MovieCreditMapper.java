package com.erdouglass.emdb.media.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.common.CreditType;
import com.erdouglass.emdb.common.comand.SaveMovieCredit;
import com.erdouglass.emdb.common.comand.UpdateMovieCredit;
import com.erdouglass.emdb.common.query.MovieCreditDto;
import com.erdouglass.emdb.media.entity.Movie;
import com.erdouglass.emdb.media.entity.MovieCredit;
import com.erdouglass.emdb.media.entity.Person;

@ApplicationScoped
public class MovieCreditMapper {
  
  @Inject
  PersonMapper mapper;
  
  public MovieCredit toMovieCredit(SaveMovieCredit command, Movie movie, Person person) {
    var credit = new MovieCredit();
    credit.movie(movie);
    credit.person(person);
    credit.type(command.type());
    credit.role(command.role());
    credit.order(command.order());
    return credit;
  }  
  
  public MovieCredit toMovieCredit(UpdateMovieCredit command, Movie movie, Person person) {
    var credit = new MovieCredit();
    credit.movie(movie);
    credit.person(person);
    credit.type(command.type());
    credit.role(command.role());
    credit.order(command.order());
    return credit;
  }
    
  public SaveMovieCredit toSaveMovieCredit(MovieCredit credit) {
    return SaveMovieCredit.builder()
        .person(mapper.toSavePerson(credit.person()))
        .type(credit.type())
        .role(credit.role())
        .order(credit.order().orElse(null))
        .build();
  }

  public MovieCreditDto toMovieCreditDto(MovieCredit credit) {
    var mcb = MovieCreditDto.builder()
        .creditId(credit.id())
        .id(credit.person().id())
        .name(credit.person().name())
        .gender(credit.person().gender().orElse(null))
        .profile(credit.person().profile().map(p -> String.format("%s.jpg", p)).orElse(null))
        .order(credit.order().orElse(null));
    if (credit.type() == CreditType.CAST) {
      mcb.character(credit.role());
    } else {
      mcb.job(credit.role());
    }
    return mcb.build();
  }
  
}