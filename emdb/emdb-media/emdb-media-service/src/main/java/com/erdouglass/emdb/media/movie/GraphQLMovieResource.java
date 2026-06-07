package com.erdouglass.emdb.media.movie;

import jakarta.inject.Inject;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Source;

import com.erdouglass.emdb.media.query.MovieResponse;
import com.erdouglass.emdb.media.query.MovieResponse.Credits;

@GraphQLApi
public class GraphQLMovieResource {
  
  @Inject
  MovieService service;
  
  @Query("findMovieById") 
  public MovieResponse findById(@Name("id") Long id) {
    return service.findById(id);
  }
  
  public Credits credits(@Source MovieResponse movie) {
    return service.findCreditsByMovieId(movie.id());
  }
}
