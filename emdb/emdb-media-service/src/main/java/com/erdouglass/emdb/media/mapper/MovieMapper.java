package com.erdouglass.emdb.media.mapper;

import jakarta.enterprise.context.ApplicationScoped;

import com.erdouglass.emdb.common.message.MovieCreateMessage;
import com.erdouglass.emdb.common.query.MovieDto;
import com.erdouglass.emdb.common.request.MovieCreateRequest;
import com.erdouglass.emdb.media.entity.Movie;

@ApplicationScoped
public class MovieMapper {
  
  public Movie toMovie(MovieCreateMessage message) {
    var movie = new Movie(message.tmdbId(), message.title(), message.status());
    movie.releaseDate(message.releaseDate());
    movie.score(message.score() == 0 ? null : message.score());
    movie.runtime(message.runtime() == 0 ? null : message.runtime());
    movie.budget(message.budget() == 0 ? null : message.budget());
    movie.revenue(message.revenue() == 0 ? null : message.revenue());
    movie.homepage(message.homepage());
    movie.originalLanguage(message.originalLanguage());
    movie.backdrop(message.backdrop());
    movie.poster(message.poster());
    movie.tagline(message.tagline());
    movie.overview(message.overview());
    return movie;
  }

  public Movie toMovie(MovieCreateRequest request) {
    var movie = new Movie(request.tmdbId(), request.title(), request.status());
    movie.releaseDate(request.releaseDate());
    movie.score(request.score() == 0 ? null : request.score());
    movie.runtime(request.runtime() == 0 ? null : request.runtime());
    movie.budget(request.budget() == 0 ? null : request.budget());
    movie.revenue(request.revenue() == 0 ? null : request.revenue());
    movie.homepage(request.homepage());
    movie.originalLanguage(request.originalLanguage());
    movie.backdrop(request.backdrop());
    movie.poster(request.poster());
    movie.tagline(request.tagline());
    movie.overview(request.overview());
    return movie;
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
        .build();
  }
  
}