package com.erdouglass.emdb.media.movie.adapter.in.graphql;

import jakarta.inject.Inject;

import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Source;

import com.erdouglass.emdb.media.movie.application.port.in.FindMovieCreditsUseCase;
import com.erdouglass.emdb.media.movie.domain.model.MoviePublicId;

@GraphQLApi
public class MovieCreditResolver {

  @Inject 
  FindMovieCreditsUseCase findCredits;
  
  @Inject
  MovieCreditMapper mapper;
  
  @Name("credits")
  @Description("Cast and crew for this title.")
  public MovieCreditResponse credits(@Source MovieResponse movie) {
    return mapper
        .toMovieCreditResponse(findCredits.findByMovieId(MoviePublicId.of(movie.id())));
  }
}
