package com.erdouglass.emdb.media.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.comand.SaveMovie.CastCredit;
import com.erdouglass.emdb.common.comand.SaveMovie.CrewCredit;
import com.erdouglass.emdb.media.entity.Credit.CreditType;
import com.erdouglass.emdb.media.entity.Movie;
import com.erdouglass.emdb.media.entity.MovieCredit;
import com.erdouglass.emdb.media.entity.Person;

@Mapper(
    componentModel = "cdi", 
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface MovieCreditMapper {
  
  void merge(MovieCredit source, @MappingTarget MovieCredit target);

  default MovieCredit toMovieCredit(Movie movie, Person person, CastCredit credit) {
    var toReturn = new MovieCredit();
    toReturn.setType(CreditType.CAST);
    toReturn.setMovie(movie);
    toReturn.setPerson(person);
    toReturn.setRole(credit.character());
    toReturn.setOrder(credit.order());
    return toReturn;
  }
  
  default MovieCredit toMovieCredit(Movie movie, Person person, CrewCredit credit) {
    var toReturn = new MovieCredit();
    toReturn.setType(CreditType.CREW);
    toReturn.setMovie(movie);
    toReturn.setPerson(person);
    toReturn.setRole(credit.job());
    return toReturn;
  }  
  
}
