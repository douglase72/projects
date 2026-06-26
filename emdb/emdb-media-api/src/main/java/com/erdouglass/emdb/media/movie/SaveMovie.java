package com.erdouglass.emdb.media.movie;

import java.time.LocalDate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.erdouglass.common.validation.DateRange;
import com.erdouglass.emdb.media.MediaConstants;
import com.erdouglass.emdb.media.image.Image;
import com.erdouglass.emdb.media.show.ShowConstants;
import com.erdouglass.emdb.media.show.ShowStatus;

public record SaveMovie(
    @NotNull @Positive Integer tmdbId,
    @NotBlank @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
    @DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate releaseDate,
    @NotNull @DecimalMin("0") @DecimalMax("10") Float score,
    @NotNull ShowStatus status,
    @PositiveOrZero Integer runtime,
    @PositiveOrZero Long budget,
    @PositiveOrZero Long revenue,
    @Valid Image backdrop,
    @Valid Image poster,
    @Size(min = 1, max = MediaConstants.URL_MAX_LENGTH) String homepage,
    @NotBlank @Size(min = MediaConstants.ISO_639_1_LENGTH, max = MediaConstants.ISO_639_1_LENGTH) String originalLanguage,
    @Size(max = ShowConstants.TAGLINE_MAX_LENGTH) String tagline,
    @Size(min = 1, max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview) {

  public static Builder builder() {
    return new Builder();
  }
  
  public static Builder builder(SaveMovie command) {
    return builder()
        .tmdbId(command.tmdbId)
        .title(command.title)
        .releaseDate(command.releaseDate)
        .score(command.score)
        .status(command.status)
        .runtime(command.runtime)
        .budget(command.budget)
        .revenue(command.revenue)
        .backdrop(command.backdrop)
        .poster(command.poster)
        .homepage(command.homepage)
        .originalLanguage(command.originalLanguage)
        .tagline(command.tagline)
        .overview(command.overview);
  }
  
  @Override
  public String toString() {
    return "SaveMovie[tmdbId=" + tmdbId
        + ", title=" + title
        + ", releaseDate=" + releaseDate
        + "]";
  }
  
  public static final class Builder extends MovieBuilder<Builder> {
    private Integer tmdbId;
    
    private Builder() {}

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