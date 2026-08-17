package com.erdouglass.emdb.media.movie.adapter.in.graphql;

import jakarta.inject.Inject;

import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Query;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.movie.application.port.in.FindMovieUseCase;
import com.erdouglass.emdb.media.movie.domain.MoviePublicId;

/// GraphQL entry point for reading titles.
///
/// Reads only. Writes go through the REST resource, so the two protocols split
/// along the CQRS seam rather than duplicating one another: GraphQL serves
/// clients that want to shape their reads, REST serves ingestion and editing.
///
/// The resolver holds no logic beyond translating an incoming id into a domain
/// value object and an absent result into a GraphQL `null`.
@GraphQLApi
public class MovieResolver {
  private static final Logger LOGGER = Logger.getLogger(MovieResolver.class);
  
  @Inject
  FindMovieUseCase findUseCase;
  
  @Inject
  MovieMapper mapper;

  /// Looks up a single title by its catalogue id.
  ///
  /// A missing title is not an error here: the method returns `null`, which
  /// GraphQL surfaces as a null field rather than as an error entry. A
  /// malformed id *is* an error, raised while parsing the id rather than after a
  /// futile lookup.
  ///
  /// @param id the catalogue id in prefixed form, e.g. `mv_42`
  /// @return the projected title, or `null` if no title carries that id
  /// @throws IllegalArgumentException if `id` is not a well-formed catalogue id
  @Query("movie")
  @Description("A single title by its catalogue id.")
  public MovieResponse movie(@Name("id") @NonNull String id) {
    var movie = findUseCase.findById(MoviePublicId.of(id))
        .map(mapper::toMovieResponse);
    movie.ifPresent(m -> LOGGER.debugf("Found: %s", m));
    return movie.orElse(null);
  }
}
