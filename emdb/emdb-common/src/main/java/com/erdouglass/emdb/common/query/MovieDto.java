package com.erdouglass.emdb.common.query;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.AbstractMovieBuilder;
import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.ShowConstants;
import com.erdouglass.emdb.common.ShowStatus;
import com.erdouglass.validation.DateRange;

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
public record MovieDto(
    @NotNull @Positive Long id,
    @NotNull @Positive Integer tmdbId,
    @NotBlank @Size(max = ShowConstants.NAME_MAX_LENGTH) String title,
    @DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate releaseDate,
    @Min(0) @Max(10) Float score,
    @NotNull @Size(max = ShowConstants.STATUS_MAX_LENGTH) ShowStatus status,
    @PositiveOrZero Integer runtime,
    @PositiveOrZero Integer budget,
    @PositiveOrZero Integer revenue,
    @Size(max = Configuration.URL_MAX_LENGTH) String homepage,
    @Size(min = Configuration.ISO_639_1_LENGTH, max = Configuration.ISO_639_1_LENGTH) String originalLanguage,
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String backdrop,
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String poster,
    @Size(max = ShowConstants.TAGLINE_MAX_LENGTH) String tagline, 
    @Size(max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview,
    @Valid Credits credits) {  
  
  public record Credits(List<@Valid MovieCreditDto> cast, List<@Valid MovieCreditDto> crew) {
    
  }
    
  public static Builder builder() {
    return new Builder();
  }
  
  public List<MovieCreditDto> cast() {
    return Optional.ofNullable(credits).map(c -> c.cast().stream().toList()).orElse(List.of());
  }
  
  public List<MovieCreditDto> crew() {
    return Optional.ofNullable(credits).map(c -> c.crew().stream().toList()).orElse(List.of());
  }
  
  @Override
  public String toString() {
    return "MovieDto[id=" + id
            + ", tmdbId=" + tmdbId
            + ", title=" + title
            + ", releaseDate=" + releaseDate
            + "]";
  }
  
  public static final class Builder extends AbstractMovieBuilder<Builder> {
    private List<MovieCreditDto> cast = new ArrayList<>();
    private List<MovieCreditDto> crew = new ArrayList<>();
    private Long id;

    private Builder() { }

    public MovieDto build() {
      Credits credits = null;
      if (!cast.isEmpty() || !crew.isEmpty()) {
        credits = new Credits(cast.stream().toList(), crew.stream().toList());
      }
      return new MovieDto(
            id,
            tmdbId,
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
            overview,
            credits);
    }
    
    public Builder cast(List<MovieCreditDto> cast) {
      this.cast = new ArrayList<>(cast);
      return this;
    }
    
    public Builder crew(List<MovieCreditDto> crew) {
      this.crew = new ArrayList<>(crew);
      return this;
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
