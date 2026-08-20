package com.erdouglass.emdb.media.person.adapter.in.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.media.person.domain.Name;

import lombok.Builder;

/// Request body for editing a person through the catalogue id.
///
/// Identical to [SavePersonRequest] apart from the required version, and the
/// version is the whole difference in behaviour: this endpoint refuses the write
/// if the person has moved on since the client read it, where ingestion does not.
///
/// Replacement semantics apply here too — an omitted optional field clears the
/// stored value rather than preserving it.
///
/// @param version the version the client last read
/// @param name the display name, required
/// @param birthDate the birth date in ISO-8601 form, empty to clear
/// @param deathDate the death date in ISO-8601 form, empty to clear
/// @param gender the gender, empty to clear
/// @param biography the biography, empty to clear
@Builder
public record UpdatePersonRequest(
    @NotNull @PositiveOrZero Long version,
    @NotBlank @Size(max = Name.MAX_LENGTH) String name,
    String birthDate,
    String deathDate,
    String gender,
    String biography) {}
