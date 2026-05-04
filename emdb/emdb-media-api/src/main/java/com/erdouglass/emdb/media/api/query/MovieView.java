package com.erdouglass.emdb.media.api.query;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.api.ShowConstants;
import com.erdouglass.emdb.media.api.ValidImage;
import com.erdouglass.validation.DateRange;

/// Lightweight projection of a movie for list and browse views.
///
/// Contains only the fields needed to render a movie card in the UI:
/// title, poster, score, and release date. Omits production metadata
/// like budget, revenue, runtime, and credits to reduce payload size
/// and database load.
public record MovieView(
    @NotNull @Positive Long id,
    @NotBlank @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
    @DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate releaseDate,
    @Min(0) @Max(10) Float score,
    @ValidImage String backdrop,
    @ValidImage String poster,
    @Size(min = 1, max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview) {}
