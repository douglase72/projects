package com.erdouglass.emdb.media.api.query;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.api.Configuration;
import com.erdouglass.emdb.common.api.ShowConstants;
import com.erdouglass.emdb.media.api.SeriesType;
import com.erdouglass.emdb.media.api.ShowStatus;
import com.erdouglass.emdb.media.api.ValidImage;
import com.erdouglass.validation.DateRange;

public record SeriesDetails(
    @NotNull @Positive Long id,
    @NotNull @Positive Integer tmdbId,
    @NotBlank @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
    @DateRange(min = ShowConstants.SERIES_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate firstAirDate,
    @DateRange(min = ShowConstants.SERIES_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate lastAirDate,    
    @Min(0) @Max(10) Float score,
    ShowStatus status,
    SeriesType type,
    @ValidImage String backdrop,
    @ValidImage String poster,
    @Size(min = 1, max = Configuration.URL_MAX_LENGTH) String homepage,
    @Size(min = Configuration.ISO_639_1_LENGTH, max = Configuration.ISO_639_1_LENGTH) String originalLanguage,
    @Size(max = ShowConstants.TAGLINE_MAX_LENGTH) String tagline,
    @Size(min = 1, max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview) {}
