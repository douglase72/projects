package com.erdouglass.emdb.media.api.command;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.api.Configuration;
import com.erdouglass.emdb.common.api.ShowConstants;
import com.erdouglass.emdb.media.api.ShowStatus;
import com.erdouglass.validation.DateRange;

/// Command to update an existing movie's mutable fields.
///
/// Unlike [SaveMovie], this command does not include `tmdbId` because
/// the movie is identified by its primary key from the URL path rather
/// than by TMDB ID. The backdrop and poster fields are `UUID` rather
/// than `Image` because updates reference existing images by their
/// internal identifier — the original TMDB path is immutable once set.
public record UpdateMovie(
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
    
    private Builder() { }

    public UpdateMovie build() {
      return new UpdateMovie(
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

    @Override
    protected Builder self() {
      return this;
    }
  }
}
