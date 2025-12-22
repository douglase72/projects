package com.erdouglass.emdb.scraper.query;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.PersonConstants;
import com.erdouglass.validation.DateRange;

/// Data Transfer Object representing the response from TMDB's Person Details endpoint.
///
/// This record maps the JSON response structure of {@code GET /person/{person_id}}.
/// It serves as the source of truth for creating detailed 
/// {@link com.erdouglass.emdb.media.entity.Person} entities.
public record TmdbPersonDto(
    @NotNull @Positive Integer id,
    @NotEmpty @Size(max = PersonConstants.NAME_MAX_LENGTH) String name,
    @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE)  LocalDate  birthday,
    @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE)  LocalDate  deathday,
    @NotNull @Min(0) @Max(3) Integer gender,
    @Size(max = Configuration.URL_MAX_LENGTH) String homepage,
    @Size(max = PersonConstants.BIRTH_PLACE_MAX_LENGTH) String place_of_birth,
    @Size(min = PersonConstants.PROFILE_MIN_LENGTH, max = PersonConstants.PROFILE_MAX_LENGTH) String profile_path,
    @Size(max = PersonConstants.BIOGRAPHY_MAX_LENGTH) String biography,
    @NotNull List<String> also_known_as) {

}
