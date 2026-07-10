package com.erdouglass.emdb.media.application.port.inbound;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.erdouglass.common.validation.DateRange;
import com.erdouglass.emdb.media.MediaConstants;
import com.erdouglass.emdb.media.SeriesType;
import com.erdouglass.emdb.media.show.ShowConstants;
import com.erdouglass.emdb.media.show.ShowStatus;

public record SeriesView(
    @NotNull @Positive Long id,
    @NotNull @Positive Long externalId,
    @NotBlank @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
    @DateRange(min = ShowConstants.SERIES_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate firstAirDate,
    @DateRange(min = ShowConstants.SERIES_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate lastAirDate,    
    @NotNull @DecimalMin("0") @DecimalMax("10") BigDecimal score,
    @NotNull ShowStatus status,
    @NotNull SeriesType type,
    @ValidImage String backdrop,
    @ValidImage String poster,
    @Size(max = MediaConstants.URL_MAX_LENGTH) String homepage,
    @Size(min = MediaConstants.ISO_639_1_LENGTH, max = MediaConstants.ISO_639_1_LENGTH) String originalLanguage,
    @Size(max = ShowConstants.TAGLINE_MAX_LENGTH) String tagline,
    @Size(max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview) {

}
