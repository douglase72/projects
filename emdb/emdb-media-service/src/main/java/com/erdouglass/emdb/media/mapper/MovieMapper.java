package com.erdouglass.emdb.media.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.common.comand.UpdateMovie;
import com.erdouglass.emdb.common.query.MovieDto;
import com.erdouglass.emdb.media.entity.Movie;

@ApplicationScoped
public class MovieMapper {
  
  @Inject
  MovieCreditMapper mapper;
  
  public Movie toMovie(SaveMovie command) {
    var movie = new Movie(command.tmdbId(), command.title());
    movie.releaseDate(command.releaseDate());
    movie.score(command.score());
    movie.status(command.status());
    movie.runtime(command.runtime());
    movie.budget(command.budget());
    movie.revenue(command.revenue());
    movie.homepage(command.homepage());
    movie.originalLanguage(command.originalLanguage());
    movie.backdrop(command.backdrop());
    movie.tmdbBackdrop(command.tmdbBackdrop());
    movie.poster(command.poster());
    movie.tmdbPoster(command.tmdbPoster());
    movie.tagline(command.tagline());
    movie.overview(command.overview());
    return movie;
  }
  
  public Movie toMovie(UpdateMovie command) {
    var movie = new Movie(command.title());
    movie.releaseDate(command.releaseDate());
    movie.score(command.score());
    movie.status(command.status());
    movie.runtime(command.runtime());
    movie.budget(command.budget());
    movie.revenue(command.revenue());
    movie.homepage(command.homepage());
    movie.originalLanguage(command.originalLanguage());
    movie.backdrop(command.backdrop());
    movie.poster(command.poster());
    movie.tagline(command.tagline());
    movie.overview(command.overview());
    return movie;
  } 
  
  public SaveMovie toSaveMovie(Movie movie) {
    return SaveMovie.builder()
        .tmdbId(movie.tmdbId())
        .title(movie.title())
        .releaseDate(movie.releaseDate().orElse(null))
        .score(movie.score().orElse(null))
        .status(movie.status().orElse(null))
        .runtime(movie.runtime().orElse(null))
        .budget(movie.budget().orElse(null))
        .revenue(movie.revenue().orElse(null))
        .homepage(movie.homepage().orElse(null))
        .originalLanguage(movie.originalLanguage().orElse(null))
        .backdrop(movie.backdrop().orElse(null))
        .tmdbBackdrop(movie.tmdbBackdrop().orElse(null))
        .poster(movie.poster().orElse(null))
        .tmdbPoster(movie.tmdbPoster().orElse(null))
        .tagline(movie.tagline().orElse(null))
        .overview(movie.overview().orElse(null))
        .credits(movie.credits().stream().map(mapper::toSaveMovieCredit).toList())
        .build();
  }
  
  public MovieDto toMovieDto(Movie movie) {
    return MovieDto.builder()
        .id(movie.id())
        .tmdbId(movie.tmdbId())
        .title(movie.title())
        .releaseDate(movie.releaseDate().orElse(null))
        .score(movie.score().orElse(null))
        .status(movie.status().orElse(null))
        .runtime(movie.runtime().orElse(null))
        .budget(movie.budget().orElse(null))
        .revenue(movie.revenue().orElse(null))
        .homepage(movie.homepage().orElse(null))
        .originalLanguage(movie.originalLanguage().orElse(null))
        .backdrop(movie.backdrop().map(p -> String.format("%s.jpg", p)).orElse(null))
        .poster(movie.poster().map(p -> String.format("%s.jpg", p)).orElse(null))
        .tagline(movie.tagline().orElse(null))
        .overview(movie.overview().orElse(null))
        .cast(movie.cast().stream().map(mapper::toMovieCreditDto).toList())
        .crew(movie.crew().stream().map(mapper::toMovieCreditDto).toList())
        .build();
  }
  
}
