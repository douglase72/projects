package com.erdouglass.emdb.scraper.movie;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.erdouglass.common.validation.DateRange;
import com.erdouglass.emdb.media.api.ShowConstants;

record Movie(
    @NotNull @Positive Integer id,
    @NotBlank String title,
    @DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate release_date,
    @NotNull @Min(0) @Max(10) Float vote_average) {

}
