package com.erdouglass.emdb.graphql;

import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Source;

import com.erdouglass.emdb.media.series.SeriesDto;
import com.erdouglass.emdb.media.series.SeriesDto.SeriesCredits;
import com.erdouglass.emdb.media.series.SeriesQueryService;

@GraphQLApi
public class SeriesResolver {

  @Inject
  SeriesQueryService service;
  
  @Query("series") 
  public SeriesDto findById(@Name("id") Long id) {
    return service.findById(id);
  }
  
  public @NotNull SeriesCredits credits(@Source SeriesDto series) {
    return service.findCreditsBySeriesId(series.id());
  }
}
