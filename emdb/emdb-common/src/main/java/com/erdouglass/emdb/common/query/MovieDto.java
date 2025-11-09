package com.erdouglass.emdb.common.query;

import java.time.LocalDate;

import com.erdouglass.emdb.common.configuration.Configuration;
import com.erdouglass.validation.DateRange;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/// Represents the public-facing Data Transfer Object (DTO) for a Movie.
///
/// This record is the primary data carrier for movie information sent to clients
/// in API query responses (e.g., `GET /api/movies/{id}`). It contains a
/// validated, client-safe view of the movie entity.
///
/// ## Key Features
/// * **Immutable:** As a Java `record`, it provides an immutable data-centric
///     representation of a movie.
/// * **Validated:** Fields are annotated with `jakarta.validation` constraints
///     (like `@NotNull`, `@Size`, `@DateRange`) to ensure data integrity
///     when this DTO is serialized in a response.
/// * **Builder Pattern:** A fluent `Builder` is provided for easy
///     construction, particularly in test environments or mapping logic.
///
/// ## Usage
/// ```java
/// MovieDto movie = MovieDto.builder()
///     .id(1L)
///     .title("Inception")
///     .releaseDate(LocalDate.of(2010, 7, 16))
///     .build();
/// ```
///
/// @param id The unique identifier for the movie.
/// @param title The title of the movie.
/// @param releaseDate The theatrical release date.
/// @param score The user score (from 0 to 10).
/// @param status The movie's release status (e.g., RELEASED, IN_PRODUCTION).
/// @param runtime The movie's runtime in minutes.
/// @param budget The movie's budget.
/// @param revenue The movie's box office revenue.
/// @param homepage The URL for the movie's official homepage.
/// @param originalLanguage The ISO 639-1 two-letter code for the original language.
/// @param backdrop The path to the backdrop image.
/// @param poster The path to the poster image.
/// @param tagline The movie's tagline.
/// @param overview A brief overview or summary of the movie.
///
/// @see com.erdouglass.emdb.common.command.MovieCreateCommand
/// @see com.erdouglass.emdb.common.command.MovieUpdateCommand
/// @see com.erdouglass.emdb.common.query.ShowDto
public record MovieDto(
		@NotNull @Positive Long id,
		@NotBlank @Size(max = ShowDto.NAME_MAX_LENGTH) String title,
		@DateRange(min = MIN_DATE, max = MAX_DATE) LocalDate releaseDate,
		@Min(0) @Max(10) Float score,
		@NotNull @Size(max = ShowDto.STATUS_MAX_LENGTH) ShowStatus status,
		@PositiveOrZero Integer runtime,
		@PositiveOrZero Integer budget,
		@PositiveOrZero Integer revenue,
		@Size(max = Configuration.URL_MAX_LENGTH) String homepage,
		@Size(min = Configuration.ISO_639_1_LENGTH, max = Configuration.ISO_639_1_LENGTH) String originalLanguage,
    @Size(min = ShowDto.POSTER_MIN_LENGTH, max = ShowDto.POSTER_MAX_LENGTH) String backdrop,
    @Size(min = ShowDto.POSTER_MIN_LENGTH, max = ShowDto.POSTER_MAX_LENGTH) String poster,
    @Size(max = ShowDto.TAGLINE_MAX_LENGTH) String tagline, 
    @Size(max = ShowDto.OVERVIEW_MAX_LENGTH) String overview) {	
	public static final String MIN_DATE = "1888-01-01";
	public static final String MAX_DATE = "2100-01-01";
	
  public static Builder builder() {
    return new Builder();
  }
  
  @Override
  public String toString() {
  	return "MovieDto[id=" + id
  			+ ", title=" + title
  			+ ", releaseDate=" + releaseDate
  			+ ", status=" + status
  			+ "]";
  }
  
  public static final class Builder extends AbstractMovieBuilder<Builder> {
    private Long id;

    private Builder() { }

    public MovieDto build() {
      return new MovieDto(
      		id, 
      		title, 
      		releaseDate,
      		score,
      		status,
      		runtime,
      		budget,
      		revenue,
      		homepage,
      		originalLanguage,
      		backdrop,
      		poster,
      		tagline,
      		overview);
    }

    public Builder id(Long id) {
      this.id = id;
      return this;
    }

    @Override
    protected Builder self() {
      return this;
    }
  }
	
}
