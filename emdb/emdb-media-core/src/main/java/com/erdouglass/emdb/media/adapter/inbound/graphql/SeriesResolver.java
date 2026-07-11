package com.erdouglass.emdb.media.adapter.inbound.graphql;

import jakarta.inject.Inject;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Source;

import com.erdouglass.emdb.media.application.port.inbound.series.SeriesQueryService;
import com.erdouglass.emdb.media.application.port.inbound.series.SeriesView;
import com.erdouglass.emdb.media.application.port.inbound.series.SeriesView.SeriesCredits;

@GraphQLApi
public class SeriesResolver {
  
  @Inject
  SeriesQueryService service;

  @Query("series") 
  public SeriesView findById(@Name("id") Long id) {
    return service.findById(id);
  }
  
  public @NonNull SeriesCredits credits(@Source SeriesView series) {
    return service.findCreditsBySeriesId(series.id());
  }
}
