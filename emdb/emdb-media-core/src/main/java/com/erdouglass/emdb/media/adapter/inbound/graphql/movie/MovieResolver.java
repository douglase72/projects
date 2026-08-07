package com.erdouglass.emdb.media.adapter.inbound.graphql;

import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;

import com.erdouglass.emdb.media.application.port.inbound.FindMovieUseCase;
import com.erdouglass.emdb.media.domain.movie.MoviePublicId;

@GraphQLApi
public class MovieResolver {
  
  @Inject
  MovieMapper mapper;

  @Inject
  FindMovieUseCase findUseCase;
  
  @Query("movie") 
  public MovieView findById(@NotBlank @Name("id") String id) {
    return findUseCase.findById(MoviePublicId.from(id))
        .map(mapper::toMovieView)
        .orElse(null);
  }  
}
