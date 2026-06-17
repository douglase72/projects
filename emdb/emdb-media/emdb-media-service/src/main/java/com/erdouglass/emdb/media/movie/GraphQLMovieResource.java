package com.erdouglass.emdb.media.movie;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Source;

import com.erdouglass.common.graphql.ResourceNotFoundException;
import com.erdouglass.emdb.media.query.MovieResponse;
import com.erdouglass.emdb.media.query.MovieResponse.Credits;
import com.erdouglass.emdb.media.query.OffsetPage;

/// GraphQL API for querying [Movie] data. Exposes a movie lookup by id and a
/// field resolver that lazily attaches cast & crew credits, so credits are
/// fetched only when a query selects them rather than on every movie read.
@GraphQLApi
public class GraphQLMovieResource {
  
  @Inject
  MovieService service;
  
  @Query("allMovies") 
  public OffsetPage<MovieResponse> findAll(@Valid @Name("query") MovieQuery query) {
    return service.findAll(query);
  }
  
  /// Looks up a single movie by its primary key.
  ///
  /// @param id the movie id
  /// @return the movie
  /// @throws ResourceNotFoundException if no movie has the given id
  @Query("movie") 
  public MovieResponse findById(@Positive @Name("id") Long id) {
    return service.findById(id);
  }
  
  /// Field resolver that supplies the `credits` field of a [MovieResponse],
  /// invoked by the GraphQL engine only when a query selects credits. The
  /// credits are not a field of [MovieResponse] itself; they are resolved
  /// separately here against the parent movie's id.
  ///
  /// @param movie the parent movie the credits belong to
  /// @return the movie's cast & crew credits
  public Credits credits(@Source MovieResponse movie) {
    return service.findCreditsByMovieId(movie.id());
  }
}
