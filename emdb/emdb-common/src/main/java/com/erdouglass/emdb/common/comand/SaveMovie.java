package com.erdouglass.emdb.common.comand;

import java.time.LocalDate;
import java.util.UUID;

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

/// Command payload containing the necessary data to save a new movie.
///
/// Includes validation constraints to ensure data integrity before persistence.
///
/// @param tmdbId           the strictly positive external TMDB identifier
/// @param title            the title of the movie
/// @param releaseDate      the official release date, bounded by [ShowConstants]
/// @param score            the user or critic score, ranging from 0 to 10
/// @param status           the current production [ShowStatus]
/// @param runtime          the length of the movie in minutes
/// @param budget           the production budget
/// @param revenue          the box office revenue
/// @param backdrop         the internal UUID of the backdrop image
/// @param poster           the internal UUID of the poster image
/// @param homepage         the official website URL
/// @param originalLanguage the ISO 639-1 language code
/// @param tagline          a short promotional tagline
/// @param overview         a detailed plot summary
public record SaveMovie(
    @NotNull @Positive Integer tmdbId,
    @NotBlank @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
    @DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate releaseDate,
    @Min(0) @Max(10) Float score,
    ShowStatus status,
    @PositiveOrZero Integer runtime,
    @PositiveOrZero Integer budget,
    @PositiveOrZero Integer revenue,
    UUID backdrop,
    UUID poster,
    @Size(min = 1, max = Configuration.URL_MAX_LENGTH) String homepage,
    @Size(min = Configuration.ISO_639_1_LENGTH, max = Configuration.ISO_639_1_LENGTH) String originalLanguage,
    @Size(max = ShowConstants.TAGLINE_MAX_LENGTH) String tagline,
    @Size(min = 1, max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview) {
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder extends AbstractMovieBuilder<Builder> {
    private UUID backdrop;
    private UUID poster;
    private Integer tmdbId;
    
    private Builder() { }

    public SaveMovie build() {
      return new SaveMovie(
            tmdbId,
            title, 
            releaseDate,
            score,
            status,
            runtime,
            budget,
            revenue,
            backdrop,
            poster,
            homepage,
            originalLanguage,
            tagline,
            overview);
    }
    
    public Builder backdrop(UUID backdrop) {
      this.backdrop = backdrop;
      return this;
    }
    
    public Builder poster(UUID poster) {
      this.poster = poster;
      return this;
    }    
    
    public Builder tmdbId(Integer tmdbId) {
      this.tmdbId = tmdbId;
      return this;
    }

    @Override
    protected Builder self() {
      return this;
    }
  }  

}
