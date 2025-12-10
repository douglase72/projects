package com.erdouglass.emdb.common.command;

import java.time.LocalDate;

import com.erdouglass.emdb.common.AbstractMovieBuilder;
import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.ShowConstants;
import com.erdouglass.emdb.common.ShowStatus;
import com.erdouglass.validation.DateRange;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/// A Data Transfer Object (DTO) representing a command to create a new movie.
///
/// This record is used as the request body for `POST /api/movies` endpoints.
/// Its primary purpose is to capture and validate all required information
/// for a new movie entity *before* it reaches the service layer.
///
/// ## Validation
/// All fields are aggressively validated using Jakarta Bean Validation annotations
/// (e.g., `@NotBlank`, `@NotNull`, `@DateRange`). This ensures that no
/// invalid movie can be created. Unlike `MovieUpdateCommand`, fields here are
/// not `Optional` as they are required for creation.
///
/// A fluent `Builder` is provided for easy construction.
public record MovieCreateCommand(
	@NotNull @Positive Integer tmdbId,
	@NotBlank @Size(max = ShowConstants.NAME_MAX_LENGTH) String title,
	@DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate releaseDate,
	@Min(0) @Max(10) Float score,
	@NotNull ShowStatus status,
	@PositiveOrZero Integer runtime,
	@PositiveOrZero Integer budget,
	@PositiveOrZero Integer revenue,
	@Size(max = Configuration.URL_MAX_LENGTH) String homepage,
	@Size(min = Configuration.ISO_639_1_LENGTH, max = Configuration.ISO_639_1_LENGTH) String originalLanguage,
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String backdrop,
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String poster,
    @Size(max = ShowConstants.TAGLINE_MAX_LENGTH) String tagline, 
    @Size(max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview) {
	
  public static Builder builder() {
    return new Builder();
  }
  
  @Override
  public String toString() {
  	return "MovieCreateCommand[tmdbId=" + tmdbId
  			+ ", title=" + title
  			+ ", releaseDate=" + releaseDate
  			+ "]";
  }
  
  public static final class Builder extends AbstractMovieBuilder<Builder> {

    private Builder() { }

    public MovieCreateCommand build() {
      return new MovieCreateCommand(
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
      		overview);
    }

    @Override
    protected Builder self() {
      return this;
    }
  }

}
