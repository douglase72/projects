package com.erdouglass.emdb.media.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.common.message.MovieCreateMessage;
import com.erdouglass.emdb.common.query.MovieDto;
import com.erdouglass.emdb.common.request.MovieCreateRequest;
import com.erdouglass.emdb.media.entity.Movie;

@ApplicationScoped
public class MovieMapper {
  
  @Inject
  MovieCreditMapper mapper;
  
  public Movie toMovie(MovieCreateMessage message) {
    var movie = new Movie(message.tmdbId(), message.title(), message.status());
    movie.releaseDate(message.releaseDate());
    movie.score(message.score());
    movie.runtime(message.runtime());
    movie.budget(message.budget());
    movie.revenue(message.revenue());
    movie.homepage(message.homepage());
    movie.originalLanguage(message.originalLanguage());
    movie.backdrop(message.backdrop());
    movie.poster(message.poster());
    movie.tagline(message.tagline());
    movie.overview(message.overview());
    return movie;
  }

  public MovieCreateMessage toMovieCreateMessage(MovieCreateRequest request) {
    return MovieCreateMessage.builder()
        .tmdbId(request.tmdbId())
        .title(request.title())
        .releaseDate(request.releaseDate())
        .score(request.score())
        .status(request.status())
        .runtime(request.runtime())
        .budget(request.budget())
        .revenue(request.revenue())
        .homepage(request.homepage())
        .originalLanguage(request.originalLanguage())
        .backdrop(request.backdrop())
        .poster(request.poster())
        .tagline(request.tagline())
        .overview(request.overview())
        .credits(request.credits())
        .build();
  }
  
  public MovieDto toMovieDto(Movie movie) {
    return MovieDto.builder()
        .id(movie.id())
        .tmdbId(movie.tmdbId())
        .title(movie.name())
        .releaseDate(movie.releaseDate().orElse(null))
        .score(movie.score().orElse(null))
        .status(movie.status())
        .runtime(movie.runtime().orElse(null))
        .budget(movie.budget().orElse(null))
        .revenue(movie.revenue().orElse(null))
        .homepage(movie.homepage().orElse(null))
        .originalLanguage(movie.originalLanguage().orElse(null))
        .backdrop(movie.backdrop().orElse(null))
        .poster(movie.poster().orElse(null))
        .tagline(movie.tagline().orElse(null))
        .overview(movie.overview().orElse(null))
        .cast(movie.cast().stream().map(mapper::toMovieCreditDto).toList())
        .crew(movie.crew().stream().map(mapper::toMovieCreditDto).toList())
        .build();
  }
  
}