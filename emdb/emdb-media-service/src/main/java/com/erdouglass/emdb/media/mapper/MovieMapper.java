package com.erdouglass.emdb.media.mapper;

import jakarta.enterprise.context.ApplicationScoped;

import com.erdouglass.emdb.common.request.MovieCreateRequest;
import com.erdouglass.emdb.media.entity.Movie;

@ApplicationScoped
public class MovieMapper {

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
  
}