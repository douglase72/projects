package com.erdouglass.emdb.media.query;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.erdouglass.common.validation.DateRange;
import com.erdouglass.emdb.media.ValidImage;
import com.erdouglass.emdb.media.show.ShowConstants;

public record PersonMovieCastCredit(
    @NotNull UUID creditId,
    @NotNull @Positive Long id,
    @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
    @DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate releaseDate,
    @NotNull @DecimalMin("0") @DecimalMax("10") Float score,
    @ValidImage String backdrop,
    @ValidImage String poster,
    @Size(max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview,
    @Size(max = ShowConstants.ROLE_MAX_LENGTH) String character, 
    @NotNull MediaType type) implements PersonCastCredit {}
