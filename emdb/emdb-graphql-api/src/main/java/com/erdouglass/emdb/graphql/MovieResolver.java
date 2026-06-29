package com.erdouglass.emdb.graphql;

import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Source;

import com.erdouglass.emdb.media.movie.MovieDto;
import com.erdouglass.emdb.media.movie.MovieDto.MovieCredits;
import com.erdouglass.emdb.media.movie.MovieQueryService;

@GraphQLApi
public class MovieResolver {
  
  @Inject
  MovieQueryService service;

  @Query("movie") 
  public MovieDto findById(@Name("id") Long id) {
    return service.findById(id);
  }
  
  public @NotNull MovieCredits credits(@Source MovieDto movie) {
    return service.findCreditsByMovieId(movie.id());
  }
}
