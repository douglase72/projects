package com.erdouglass.emdb.scraper.query;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.SeriesType;
import com.erdouglass.emdb.common.ShowConstants;
import com.erdouglass.emdb.common.ShowStatus;

public record TmdbSeries(
    @NotNull @Positive Integer id,
    @NotBlank @Size(max = ShowConstants.TITLE_MAX_LENGTH) String name,
    @Min(0) @Max(10) Float vote_average, 
    ShowStatus status,
    SeriesType type,
    @Size(max = Configuration.URL_MAX_LENGTH) String homepage,
    @Size(min = Configuration.ISO_639_1_LENGTH, max = Configuration.ISO_639_1_LENGTH) String original_language,
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String backdrop_path,
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String poster_path,
    @Size(max = ShowConstants.TAGLINE_MAX_LENGTH) String tagline,
    @Size(max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview) {

}
