package com.erdouglass.emdb.scraper.mapper;

import com.erdouglass.emdb.common.command.MovieCreateCommand;
import com.erdouglass.emdb.scraper.dto.TmdbMovie;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TmdbMovieMapper {

  public MovieCreateCommand toMovieCreateCommand(TmdbMovie tmdbMovie) {
    return MovieCreateCommand.builder()
        .tmdbId(tmdbMovie.id())
        .title(tmdbMovie.title())
        .releaseDate(tmdbMovie.release_date())
        .score(tmdbMovie.vote_average())
        .status(tmdbMovie.status())
        .runtime(tmdbMovie.runtime())
        .budget(tmdbMovie.budget())
        .revenue(tmdbMovie.revenue())
        .homepage(tmdbMovie.homepage())
        .originalLanguage(tmdbMovie.original_language())
        .backdrop(tmdbMovie.backdrop_path())
        .poster(tmdbMovie.poster_path())
        .tagline(tmdbMovie.tagline())
        .overview(tmdbMovie.overview())
        .build();
  }
  
}
