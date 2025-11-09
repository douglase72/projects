package com.erdouglass.emdb.common.command;

import java.time.LocalDate;
import java.util.Optional;

import com.erdouglass.emdb.common.configuration.Configuration;
import com.erdouglass.emdb.common.query.MovieDto;
import com.erdouglass.emdb.common.query.ShowDto;
import com.erdouglass.emdb.common.query.ShowStatus;
import com.erdouglass.validation.DateRange;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/// A Data Transfer Object (DTO) representing a command to partially update an
/// existing movie.
///
/// This record is designed specifically for `PATCH /api/movies/{id}` endpoints.
///
/// ## `Optional`-Based Design
/// Every field is wrapped in `java.util.Optional`. This allows the API to
/// unambiguously distinguish between:
/// 1.  **Field Not Provided:** The field is `Optional.empty()`. (e.g., JSON `{}`)
///     This field should **not** be updated in the entity.
/// 2.  **Field Provided as `null`:** The field is `Optional.ofNullable(null)`.
///     (e.g., JSON `{"tagline": null}`)
///     This field **should** be updated to `null` in the entity.
/// 3.  **Field Provided with Value:** The field is `Optional.of("New Value")`.
///     (e.g., JSON `{"title": "New Title"}`)
///     This field **should** be updated to the new value.
///
/// Validation annotations are applied to the value *inside* the `Optional` and
/// are only triggered if the `Optional` is present.
///
/// A fluent `Builder` is provided for easy construction in test or service layers.
///
/// @see com.erdouglass.emdb.common.command.MovieCreateCommand
/// @see com.erdouglass.emdb.common.query.MovieDto
public record MovieUpdateCommand(
		Optional<@Size(max = ShowDto.NAME_MAX_LENGTH) String> title,
		Optional<@DateRange(min = MovieDto.MIN_DATE, max = MovieDto.MAX_DATE) LocalDate> releaseDate,
		Optional<@Min(0) @Max(10) Float> score,
		Optional<ShowStatus> status,
    Optional<@PositiveOrZero Integer> runtime,
    Optional<@PositiveOrZero Integer> budget,
    Optional<@PositiveOrZero Integer> revenue,
    Optional<@Size(max = Configuration.URL_MAX_LENGTH) String> homepage,
    Optional<@Size(min = Configuration.ISO_639_1_LENGTH, max = Configuration.ISO_639_1_LENGTH) String> originalLanguage,
    Optional<@Size(min = ShowDto.POSTER_MIN_LENGTH, max = ShowDto.POSTER_MAX_LENGTH) String> backdrop,
    Optional<@Size(min = ShowDto.POSTER_MIN_LENGTH, max = ShowDto.POSTER_MAX_LENGTH) String> poster,
    Optional<@Size(max = ShowDto.TAGLINE_MAX_LENGTH) String> tagline,
    Optional<@Size(max = ShowDto.OVERVIEW_MAX_LENGTH) String> overview) {
	
  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
  	private Optional<String> title = Optional.empty();
  	private Optional<LocalDate> releaseDate = Optional.empty();
  	private Optional<Float> score = Optional.empty();
  	private Optional<ShowStatus> status = Optional.empty();
    private Optional<Integer> runtime = Optional.empty();
    private Optional<Integer> budget = Optional.empty();
    private Optional<Integer> revenue = Optional.empty();
    private Optional<String> homepage = Optional.empty();
    private Optional<String> originalLanguage = Optional.empty();
    private Optional<String> backdrop = Optional.empty();
    private Optional<String> poster = Optional.empty();
    private Optional<String> tagline = Optional.empty();
    private Optional<String> overview = Optional.empty();  	
  	
  	private Builder() { }
  	
  	public MovieUpdateCommand build() {
      return new MovieUpdateCommand(
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
          overview
      );
  	}
  	
    public Builder title(String title) {
      this.title = Optional.ofNullable(title);
      return this;
    }
    
    public Builder releaseDate(LocalDate releaseDate) {
      this.releaseDate = Optional.ofNullable(releaseDate);
      return this;
    }
    
    public Builder score(Float score) {
      this.score = Optional.ofNullable(score);
      return this;
    }
    
    public Builder status(ShowStatus status) {
      this.status = Optional.ofNullable(status);
      return this;
    }
    
    public Builder runtime(Integer runtime) {
      this.runtime = Optional.ofNullable(runtime);
      return this;
    }
    
    public Builder budget(Integer budget) {
      this.budget = Optional.ofNullable(budget);
      return this;
    }
    
    public Builder revenue(Integer revenue) {
      this.revenue = Optional.ofNullable(revenue);
      return this;
    }
    
    public Builder homepage(String homepage) {
      this.homepage = Optional.ofNullable(homepage);
      return this;
    }
    
    public Builder originalLanguage(String originalLanguage) {
      this.originalLanguage = Optional.ofNullable(originalLanguage);
      return this;
    }
    
    public Builder backdrop(String backdrop) {
      this.backdrop = Optional.ofNullable(backdrop);
      return this;
    }
    
    public Builder poster(String poster) {
      this.poster = Optional.ofNullable(poster);
      return this;
    }
    
    public Builder tagline(String tagline) {
      this.tagline = Optional.ofNullable(tagline);
      return this;
    }
    
    public Builder overview(String overview) {
      this.overview = Optional.ofNullable(overview);
      return this;
    }
  }

}
