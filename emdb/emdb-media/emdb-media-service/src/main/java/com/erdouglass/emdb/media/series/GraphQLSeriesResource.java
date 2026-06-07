package com.erdouglass.emdb.media.series;

import jakarta.inject.Inject;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Source;

import com.erdouglass.emdb.media.query.SeriesResponse;
import com.erdouglass.emdb.media.query.SeriesResponse.Credits;

@GraphQLApi
public class GraphQLSeriesResource {

  @Inject
  SeriesMapper mapper;
  
  @Inject
  SeriesService service;
  
  @Query("findSeriesById") 
  public SeriesResponse findById(@Name("id") Long id) {
    return service.findById(id);
  }
  
  public Credits credits(@Source SeriesResponse series) {
    return service.findCreditsBySeriesId(series.id());
  }
}
