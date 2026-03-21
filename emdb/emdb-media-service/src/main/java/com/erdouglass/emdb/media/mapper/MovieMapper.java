package com.erdouglass.emdb.media.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.common.comand.UpdateMovie;
import com.erdouglass.emdb.media.dto.SaveResult;
import com.erdouglass.emdb.media.entity.Movie;
import com.erdouglass.emdb.media.proto.v1.CastCreditRequest;
import com.erdouglass.emdb.media.proto.v1.CreditRequest;
import com.erdouglass.emdb.media.proto.v1.CrewCreditRequest;
import com.erdouglass.emdb.media.proto.v1.MovieResponse;
import com.erdouglass.emdb.media.proto.v1.SaveMovieRequest;
import com.erdouglass.emdb.media.proto.v1.SaveMovieResponse;
import com.erdouglass.emdb.media.proto.v1.SavePersonRequest;
import com.erdouglass.emdb.media.proto.v1.UpdateMovieCommand;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface MovieMapper {
  
  void merge(SaveMovie command, @MappingTarget Movie movie);
  
  void merge(UpdateMovie command, @MappingTarget Movie movie);
  
  SaveMovie.Credits toCredits(CreditRequest request);
  
  SaveMovie.CastCredit toCastCredit(CastCreditRequest request);
  
  SaveMovie.CrewCredit toCrewCredit(CrewCreditRequest request);
  
  @BeanMapping(builder = @Builder(disableBuilder = true))
  SavePerson toSavePerson(SavePersonRequest request);
  
  @BeanMapping(builder = @Builder(disableBuilder = true))
  SaveMovie toSaveMovie(SaveMovieRequest request);
  
  SaveMovie toSaveMovie(Movie movie);
  
  MovieResponse toMovieResponse(Movie movie);
  
  @BeanMapping(builder = @Builder(disableBuilder = true))
  UpdateMovie toUpdateMovie(UpdateMovieCommand command);
  
  @Mapping(source = "entity", target = "movie")
  SaveMovieResponse toSaveMovieResponse(SaveResult<Movie> result);
  
  Movie toMovie(SaveMovie command);
  
}
