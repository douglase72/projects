package com.erdouglass.emdb.media.movie.adapter.in.rest;

import java.math.BigDecimal;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.media.kernel.Title;

import lombok.Builder;

/// Request body for editing a title through the catalogue id.
///
/// Identical to [SaveMovieRequest] apart from the required version, and the
/// version is the whole difference in behaviour: this endpoint refuses the write
/// if the title has moved on since the client read it, where ingestion does not.
///
/// Replacement semantics apply here too — an omitted optional field clears the
/// stored value rather than preserving it.
///
/// @param version the version the client last read
/// @param title the display title, required
/// @param releaseDate the release date in ISO-8601 form, empty to clear
/// @param score the rating from 0 to 10, empty to clear
/// @param originalLanguage the ISO 639-1 code, empty to clear
/// @param overview the synopsis, empty to clear
@Builder
public record UpdateMovieRequest(
    @NotNull @PositiveOrZero Long version, 
    @NotBlank @Size(max = Title.MAX_LENGTH) String title,
    String releaseDate,
    @Min(0) @Max(10) BigDecimal score,
    @Pattern(regexp = "[a-z]{2}") String originalLanguage,
    String overview) {}
