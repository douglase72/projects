package com.erdouglass.emdb.media.mapper;

import jakarta.data.page.Page;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.common.comand.UpdateMovie;
import com.erdouglass.emdb.common.query.MovieQueryParameters;
import com.erdouglass.emdb.common.query.MovieView;
import com.erdouglass.emdb.media.dto.SaveResult;
import com.erdouglass.emdb.media.entity.Movie;
import com.erdouglass.emdb.media.proto.v1.FindAllMovieRequest;
import com.erdouglass.emdb.media.proto.v1.MoviePageResponse;
import com.erdouglass.emdb.media.proto.v1.MovieResponse;
import com.erdouglass.emdb.media.proto.v1.MovieViewResponse;
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
public interface MovieMapper extends CommonMapper {
  
  // Request
  
  @ShowImageMapping
  void merge(SaveMovie command, @MappingTarget Movie movie);
  void merge(UpdateMovie command, @MappingTarget Movie movie);

  @BeanMapping(builder = @Builder(disableBuilder = true))
  SaveMovie toSaveMovie(SaveMovieRequest request);
  
  @BeanMapping(builder = @Builder(disableBuilder = true))
  @Mapping(source = "movie", target = "backdrop", qualifiedByName = "backdropToImage")
  @Mapping(source = "movie", target = "poster", qualifiedByName = "posterToImage")
  SaveMovie toSaveMovie(Movie movie);
    
  @InheritConfiguration(name = "merge")
  Movie toMovie(SaveMovie command);
  
  MovieQueryParameters toMovieQueryParameters(FindAllMovieRequest request);
  
  @BeanMapping(builder = @Builder(disableBuilder = true))
  UpdateMovie toUpdateMovie(UpdateMovieCommand command);
    
  // Response
  
  @Mapping(source = "entity", target = "movie")
  SaveMovieResponse toSaveMovieResponse(SaveResult<Movie> result);
  
  MovieResponse toMovieResponse(Movie movie);
  
  MovieViewResponse toMovieViewResponse(MovieView view);
  
  default MoviePageResponse toMoviePageResponse(Page<MovieView> page) {
    var builder = MoviePageResponse.newBuilder()
        .setPage((int) page.pageRequest().page())
        .setSize(page.pageRequest().size())
        .setHasNext(page.hasNext());
    page.content().stream()
        .map(this::toMovieViewResponse)
        .forEach(builder::addResults);
    return builder.build();
  }
}
