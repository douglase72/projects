package com.erdouglass.emdb.common.query;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.Gender;
import com.erdouglass.emdb.common.PersonConstants;
import com.erdouglass.emdb.common.ValidImage;
import com.erdouglass.validation.DateRange;

/// Lightweight projection of a person for paginated list responses.
///
/// Omits fields like biography and homepage that are only needed
/// in detail views.
public record PersonView(
    @NotNull @Positive Long id,
    @NotBlank @Size(max = PersonConstants.NAME_MAX_LENGTH) String name,
    @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE) LocalDate birthDate,
    Gender gender,
    @ValidImage String profile) {}
