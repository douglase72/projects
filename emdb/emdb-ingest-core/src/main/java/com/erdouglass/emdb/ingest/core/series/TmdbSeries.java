package com.erdouglass.emdb.ingest.core.series;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.media.MediaConstants;
import com.erdouglass.emdb.media.series.SeriesType;
import com.erdouglass.emdb.media.show.ShowConstants;
import com.erdouglass.emdb.media.show.ShowStatus;

record TmdbSeries(    
    @NotNull @Positive Integer id,
    @NotBlank @Size(max = ShowConstants.TITLE_MAX_LENGTH) String name,
    @NotNull @DecimalMin("0") @DecimalMax("10") Float vote_average,
    @NotNull ShowStatus status,
    @NotNull SeriesType type,
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String backdrop_path,
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String poster_path,
    @Size(min = 1, max = MediaConstants.URL_MAX_LENGTH) String homepage,
    @NotBlank @Size(min = MediaConstants.ISO_639_1_LENGTH, max = MediaConstants.ISO_639_1_LENGTH) String original_language,
    @Size(max = ShowConstants.TAGLINE_MAX_LENGTH) String tagline,
    @Size(min = 1, max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview) {

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[id=" + id
        + ", name=" + name
        + "]";
  }
}
