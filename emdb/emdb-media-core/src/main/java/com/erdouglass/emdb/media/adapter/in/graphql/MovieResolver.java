package com.erdouglass.emdb.media.adapter.in.graphql;

import jakarta.inject.Inject;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;

import com.erdouglass.emdb.media.application.port.in.MovieQueryService;
import com.erdouglass.emdb.media.application.port.in.MovieView;

@GraphQLApi
public class MovieResolver {
  
  @Inject
  MovieQueryService service;

  @Query("movie") 
  public MovieView findById(@Name("id") Long id) {
    return service.findById(id);
  }
}
