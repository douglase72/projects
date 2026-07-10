package com.erdouglass.emdb.media.adapter.inbound.graphql;

import jakarta.inject.Inject;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Source;

import com.erdouglass.emdb.media.application.port.inbound.MovieQueryService;
import com.erdouglass.emdb.media.application.port.inbound.MovieView;
import com.erdouglass.emdb.media.application.port.inbound.MovieView.MovieCredits;

@GraphQLApi
public class MovieResolver {
  
  @Inject
  MovieQueryService service;

  @Query("movie") 
  public MovieView findById(@Name("id") Long id) {
    return service.findById(id);
  }
  
  public @NonNull MovieCredits credits(@Source MovieView movie) {
    return service.findCreditsByMovieId(movie.id());
  }
}
