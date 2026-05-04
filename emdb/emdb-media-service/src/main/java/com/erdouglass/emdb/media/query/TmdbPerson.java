package com.erdouglass.emdb.media.query;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.api.Configuration;
import com.erdouglass.emdb.media.api.PersonConstants;
import com.erdouglass.validation.DateRange;

public record TmdbPerson(
    @NotNull @Positive Integer id,
    @NotEmpty @Size(max = PersonConstants.NAME_MAX_LENGTH) String name,
    @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE) LocalDate birthday,
    @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE) LocalDate deathday,
    @Min(0) @Max(3) Integer gender,
    @Size(min = PersonConstants.PROFILE_MIN_LENGTH, max = PersonConstants.PROFILE_MAX_LENGTH) String profile_path,
    @Size(max = Configuration.URL_MAX_LENGTH) String homepage,
    @Size(max = PersonConstants.BIRTH_PLACE_MAX_LENGTH) String place_of_birth,
    @Size(max = PersonConstants.BIOGRAPHY_MAX_LENGTH) String biography) {}