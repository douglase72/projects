package com.erdouglass.emdb.media.adapter.inbound.movie;

import jakarta.inject.Inject;

import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Query;

import com.erdouglass.emdb.media.application.port.inbound.movie.FindMovieUseCase;
import com.erdouglass.emdb.media.domain.movie.MoviePublicId;

@GraphQLApi
public class MovieResolver {
  
  @Inject
  FindMovieUseCase findUseCase;

  @Query("movie")
  @Description("A single title by its catalogue id.")
  public MovieView movie(@Name("id") @NonNull String id) {
    return findUseCase.findById(MoviePublicId.of(id));
  }
}
