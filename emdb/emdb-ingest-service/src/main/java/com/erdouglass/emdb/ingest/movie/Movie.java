package com.erdouglass.emdb.ingest.movie;

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
import com.erdouglass.emdb.common.command.ShowConstants;
import com.erdouglass.emdb.common.command.ShowStatus;

/// JSON payload returned by the TMDB `/movie/{id}` endpoint.
///
/// Field names use snake_case to match the TMDB API response directly, which
/// lets Jackson bind without any field-name overrides. This is intentionally
/// distinct from the persistence-side [com.erdouglass.emdb.media.movie.Movie]
/// entity — the two are mapped together by [MovieMapper] before the command
/// is published to the media service.
///
/// Bean Validation constraints enforce the contract the downstream services
/// expect, so a malformed or surprising TMDB response fails fast at the
/// ingest boundary rather than corrupting later stages.
record Movie(
    @NotNull @Positive Integer id,
    @NotBlank @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
    @DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate release_date,
    @NotNull @Min(0) @Max(10) Float vote_average, 
    @NotNull ShowStatus status,
    @PositiveOrZero Integer runtime,
    @PositiveOrZero Integer budget,
    @PositiveOrZero Integer revenue,
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String backdrop_path,
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String poster_path,
    @Size(min = 1, max = Configuration.URL_MAX_LENGTH) String homepage,
    @NotBlank @Size(min = Configuration.ISO_639_1_LENGTH, max = Configuration.ISO_639_1_LENGTH) String original_language,
    @Size(max = ShowConstants.TAGLINE_MAX_LENGTH) String tagline,
    @Size(min = 1, max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview) {}
