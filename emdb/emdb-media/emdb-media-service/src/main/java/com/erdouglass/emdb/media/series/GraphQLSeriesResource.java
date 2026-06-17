package com.erdouglass.emdb.media.series;

import jakarta.inject.Inject;
import jakarta.validation.Valid;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Source;

import com.erdouglass.common.graphql.ResourceNotFoundException;
import com.erdouglass.emdb.media.query.OffsetPage;
import com.erdouglass.emdb.media.query.SeriesResponse;
import com.erdouglass.emdb.media.query.SeriesResponse.Credits;

/// GraphQL API for querying [Series] data. Exposes a series lookup by id and a
/// field resolver that lazily attaches cast & crew credits, so credits are
/// fetched only when a query selects them rather than on every series read.
@GraphQLApi
public class GraphQLSeriesResource {
  
  @Inject
  SeriesService service;
  
  @Query("allSeries") 
  public OffsetPage<SeriesResponse> findAll(@Valid @Name("query") SeriesQuery query) {
    return service.findAll(query);
  }
  
  /// Looks up a single series by its primary key.
  ///
  /// @param id the series id
  /// @return the series
  /// @throws ResourceNotFoundException if no series has the given id
  @Query("series") 
  public SeriesResponse findById(@Name("id") Long id) {
    return service.findById(id);
  }
  
  /// Field resolver that supplies the `credits` field of a [SeriesResponse],
  /// invoked by the GraphQL engine only when a query selects credits. The
  /// credits are not a field of [SeriesResponse] itself; they are resolved
  /// separately here against the parent series' id.
  ///
  /// @param series the parent series the credits belong to
  /// @return the series' cast & crew credits
  public Credits credits(@Source SeriesResponse series) {
    return service.findCreditsBySeriesId(series.id());
  }
}
