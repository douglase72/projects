package com.erdouglass.emdb.media.adapter.inbound.graphql;

import jakarta.inject.Inject;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Source;

import com.erdouglass.emdb.media.application.port.inbound.series.QuerySeriesUseCase;
import com.erdouglass.emdb.media.application.port.inbound.series.SeriesView;
import com.erdouglass.emdb.media.application.port.inbound.series.SeriesView.SeriesCredits;

@GraphQLApi
public class SeriesResolver {
  
  @Inject
  QuerySeriesUseCase queryUseCase;

  @Query("series") 
  public SeriesView findById(@Name("id") Long id) {
    return queryUseCase.findById(id);
  }
  
  public @NonNull SeriesCredits credits(@Source SeriesView series) {
    return queryUseCase.findCreditsBySeriesId(series.id());
  }
}
