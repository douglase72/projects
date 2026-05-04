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

import com.erdouglass.emdb.media.api.Image;
import com.erdouglass.emdb.media.api.command.SaveMovie;
import com.erdouglass.emdb.media.api.command.UpdateMovie;
import com.erdouglass.emdb.media.api.query.MovieQueryParameters;
import com.erdouglass.emdb.media.api.query.MovieView;
import com.erdouglass.emdb.media.entity.Movie;
import com.erdouglass.emdb.media.proto.v1.FindAllMovieRequest;
import com.erdouglass.emdb.media.proto.v1.MoviePageResponse;
import com.erdouglass.emdb.media.proto.v1.MovieResponse;
import com.erdouglass.emdb.media.proto.v1.MovieViewResponse;
import com.erdouglass.emdb.media.proto.v1.SaveMovieRequest;
import com.erdouglass.emdb.media.proto.v1.SaveMovieResponse;
import com.erdouglass.emdb.media.proto.v1.UpdateMovieCommand;
import com.erdouglass.emdb.media.query.SaveResult;
import com.erdouglass.emdb.media.query.TmdbMovie;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface MovieMapper extends CommonMapper {
    
  @ShowImageMapping
  void merge(SaveMovie command, @MappingTarget Movie movie);
  
  @Mapping(target = "tmdbId", ignore = true)
  @Mapping(target = "tmdbBackdrop", ignore = true)
  @Mapping(target = "tmdbPoster", ignore = true)
  void merge(UpdateMovie command, @MappingTarget Movie movie);

  @BeanMapping(builder = @Builder(disableBuilder = true))
  SaveMovie toSaveMovie(SaveMovieRequest request);
  
  @BeanMapping(builder = @Builder(disableBuilder = true))
  @Mapping(source = "movie", target = "backdrop", qualifiedByName = "backdropToImage")
  @Mapping(source = "movie", target = "poster", qualifiedByName = "posterToImage")
  SaveMovie toSaveMovie(Movie movie);
  
  @BeanMapping(builder = @Builder(disableBuilder = true))
  @Mapping(source = "movie.id", target = "tmdbId")
  @Mapping(source = "movie.release_date", target = "releaseDate")
  @Mapping(source = "movie.vote_average", target = "score")
  @Mapping(source = "movie.original_language", target = "originalLanguage")
  SaveMovie toSaveMovie(TmdbMovie movie, Image backdrop, Image poster);
    
  @InheritConfiguration(name = "merge")
  Movie toMovie(SaveMovie command);
  
  MovieQueryParameters toMovieQueryParameters(FindAllMovieRequest request);
  
  @BeanMapping(builder = @Builder(disableBuilder = true))
  UpdateMovie toUpdateMovie(UpdateMovieCommand command);
      
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
