package com.erdouglass.emdb.media.adapter.inbound.movie;

import jakarta.inject.Inject;

import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Query;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.application.port.inbound.movie.FindMovieUseCase;
import com.erdouglass.emdb.media.domain.movie.MoviePublicId;

@GraphQLApi
public class MovieResolver {
  private static final Logger LOGGER = Logger.getLogger(MovieResolver.class);
  
  @Inject
  FindMovieUseCase findUseCase;

  @Query("movie")
  @Description("A single title by its catalogue id.")
  public MovieView movie(@Name("id") @NonNull String id) {
    var movie = findUseCase.findById(MoviePublicId.of(id));
    movie.ifPresent(m -> LOGGER.debugf("Found: %s", m));
    return movie.orElse(null);
  }
}
