package com.erdouglass.emdb.media.mapper;

import com.erdouglass.emdb.common.command.MovieCreateCommand;
import com.erdouglass.emdb.common.query.MovieDto;
import com.erdouglass.emdb.media.entity.Movie;

import jakarta.enterprise.context.ApplicationScoped;

/// A CDI bean responsible for mapping between `Movie` DTOs and `Movie` entities.
///
/// This class decouples the web layer (DTOs) from the persistence layer (entities),
/// allowing each to evolve independently.
///
/// ## Responsibilities
/// * `toMovie(MovieCreateCommand)`: Converts a create-request DTO into a new
///     `Movie` entity, ready to be persisted.
/// * `toMovieDto(Movie)`: Converts a `Movie` entity from the database into a
///     client-safe `MovieDto` for API responses.
/// * Encapsulates mapping logic, such as handling default values (e.g., `0` to `null`)
///     and unwrapping `Optional` fields from the entity.
///
/// @see com.erdouglass.emdb.media.entity.Movie
/// @see com.erdouglass.emdb.common.command.MovieCreateCommand
/// @see com.erdouglass.emdb.common.query.MovieDto
@ApplicationScoped
public class MovieMapper {
	
  public Movie toMovie(MovieCreateCommand command) {
    var movie = new Movie(command.tmdbId(), command.title(), command.status());
    movie.releaseDate(command.releaseDate());
    movie.score(command.score() == 0 ? null : command.score());
    movie.runtime(command.runtime() == 0 ? null : command.runtime());
    movie.budget(command.budget() == 0 ? null : command.budget());
    movie.revenue(command.revenue() == 0 ? null : command.revenue());
    movie.homepage(command.homepage());
    movie.originalLanguage(command.originalLanguage());
    movie.backdrop(command.backdrop());
    movie.poster(command.poster());
    movie.tagline(command.tagline());
    movie.overview(command.overview());
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
