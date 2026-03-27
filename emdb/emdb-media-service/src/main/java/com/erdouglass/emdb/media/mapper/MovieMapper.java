package com.erdouglass.emdb.media.mapper;

import java.util.List;

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
import com.erdouglass.emdb.common.comand.UpdateMovie;
import com.erdouglass.emdb.media.dto.SaveResult;
import com.erdouglass.emdb.media.entity.Movie;
import com.erdouglass.emdb.media.entity.MovieCredit;
import com.erdouglass.emdb.media.proto.v1.MovieCastCreditResponse;
import com.erdouglass.emdb.media.proto.v1.MovieCreditResponse;
import com.erdouglass.emdb.media.proto.v1.MovieCrewCreditResponse;
import com.erdouglass.emdb.media.proto.v1.MovieResponse;
import com.erdouglass.emdb.media.proto.v1.SaveMovieRequest;
import com.erdouglass.emdb.media.proto.v1.SaveMovieResponse;
import com.erdouglass.emdb.media.proto.v1.UpdateMovieCommand;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface MovieMapper {
  
  @Mapping(target = "credits", ignore = true)
  void merge(SaveMovie command, @MappingTarget Movie movie);  
  void merge(UpdateMovie command, @MappingTarget Movie movie);
  
  @BeanMapping(builder = @Builder(disableBuilder = true))
  SaveMovie toSaveMovie(SaveMovieRequest request);
  
  @Mapping(target = "credits", ignore = true)
  SaveMovie toSaveMovie(Movie movie);
  
  @Mapping(target = "credits", ignore = true)
  Movie toMovie(SaveMovie command);
  
  @BeanMapping(builder = @Builder(disableBuilder = true))
  UpdateMovie toUpdateMovie(UpdateMovieCommand command);
  
  default MovieCreditResponse toMovieCreditResponse(List<MovieCredit> credits) {
    if (credits == null || credits.isEmpty()) return MovieCreditResponse.getDefaultInstance();
    var builder = MovieCreditResponse.newBuilder();
    for (var credit : credits) {
        switch (credit.getType()) {
            case CAST -> builder.addCast(toMovieCastCreditResponse(credit));
            case CREW -> builder.addCrew(toMovieCrewCreditResponse(credit));
        }
    }
    return builder.build();
  }

  @Mapping(source = "id", target = "creditId")
  @Mapping(source = "person.id", target = "id")
  @Mapping(source = "person.name", target = "name")
  @Mapping(source = "person.gender", target = "gender")
  @Mapping(source = "person.profile", target = "profile")
  @Mapping(source = "role", target = "character")
  MovieCastCreditResponse toMovieCastCreditResponse(MovieCredit credit);

  @Mapping(source = "id", target = "creditId")
  @Mapping(source = "person.id", target = "id")
  @Mapping(source = "person.name", target = "name")
  @Mapping(source = "person.gender", target = "gender")
  @Mapping(source = "person.profile", target = "profile")
  @Mapping(source = "role", target = "job")
  MovieCrewCreditResponse toMovieCrewCreditResponse(MovieCredit credit);  
  
  MovieResponse toMovieResponse(Movie movie);
  
  @Mapping(source = "entity", target = "movie")
  SaveMovieResponse toSaveMovieResponse(SaveResult<Movie> result);
    
}
