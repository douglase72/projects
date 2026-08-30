package com.erdouglass.emdb.media.movie.adapter.in.graphql;

import jakarta.inject.Inject;

import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Query;

import com.erdouglass.emdb.media.movie.application.port.in.FindMovieUseCase;
import com.erdouglass.emdb.media.movie.domain.model.MoviePublicId;

@GraphQLApi
public class MovieResolver {

  @Inject
  FindMovieUseCase findUseCase;
  
  @Inject
  MovieMapper mapper;
  
  @Query("movie")
  @Description("A single title by its catalogue id.")
  public MovieResponse movie(@Name("id") @NonNull String id) {
    return findUseCase.findById(MoviePublicId.of(id))
        .map(mapper::toMovieResponse)
        .orElse(null);
  }
}
