package com.erdouglass.emdb.media.api;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.erdouglass.common.validation.DateRange;
import com.erdouglass.emdb.common.Configuration;

public record SaveMovie(
    @NotNull @Positive Integer tmdbId,
    @NotBlank @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
    @DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate releaseDate,
    @NotNull @Min(0) @Max(10) Float score,
    @NotNull ShowStatus status,
    @PositiveOrZero Integer runtime,
    @PositiveOrZero Integer budget,
    @PositiveOrZero Integer revenue,
    String tmdbBackdrop,
    String tmdbPoster,
    @Size(min = 1, max = Configuration.URL_MAX_LENGTH) String homepage,
    @NotBlank @Size(min = Configuration.ISO_639_1_LENGTH, max = Configuration.ISO_639_1_LENGTH) String originalLanguage,
    @Size(max = ShowConstants.TAGLINE_MAX_LENGTH) String tagline,
    @Size(min = 1, max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview) implements SaveCommand {

  public static Builder builder() {
    return new Builder();
  }
  
  @Override
  public String toString() {
    return "SaveMovie[tmdbId=" + tmdbId
        + ", title=" + title
        + ", releaseDate=" + releaseDate
        + "]";
  }
  
  public static Builder builder(final SaveMovie command) {
    return builder()
        .tmdbId(command.tmdbId)
        .title(command.title)
        .releaseDate(command.releaseDate)
        .score(command.score)
        .status(command.status)
        .runtime(command.runtime)
        .budget(command.budget)
        .revenue(command.revenue)
        .backdrop(command.tmdbBackdrop)
        .poster(command.tmdbPoster)
        .homepage(command.homepage)
        .originalLanguage(command.originalLanguage)
        .tagline(command.tagline)
        .overview(command.overview);
  }
  
  public static final class Builder extends MovieBuilder<Builder> {
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
    
    public Builder tmdbId(final Integer tmdbId) {
      this.tmdbId = tmdbId;
      return this;
    }

    @Override
    protected Builder self() {
      return this;
    }
  }
}
