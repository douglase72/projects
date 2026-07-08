package com.erdouglass.emdb.media.adapter.in.graphql;

import jakarta.inject.Inject;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;

import com.erdouglass.emdb.media.application.port.in.SeriesQueryService;
import com.erdouglass.emdb.media.application.port.in.SeriesView;

@GraphQLApi
public class SeriesResolver {
  
  @Inject
  SeriesQueryService service;

  @Query("series") 
  public SeriesView findById(@Name("id") Long id) {
    return service.findById(id);
  }
}
