package com.erdouglass.emdb.media.person.application.port.out;

import java.time.LocalDate;

import com.erdouglass.emdb.media.person.domain.Gender;
import com.erdouglass.emdb.media.person.domain.Person;

/// The read model of a person, as clients see it.
///
/// Built directly by the query repository rather than mapped from the aggregate,
/// so reads never load or rehydrate a [Person]. That is the point of the split:
/// the projection can select exactly the columns it needs and can change shape
/// without touching the domain.
///
/// Fields are nullable primitives rather than domain value objects or
/// `Optional`, because this type crosses a serialization boundary — absent
/// fields become JSON nulls.
///
/// Lock state is deliberately absent; it governs writes and is not part of the
/// public read surface.
///
/// @param id the catalogue id in prefixed form, e.g. `pr_42`
/// @param version the current optimistic-locking version, to be sent back on a
///        subsequent write
/// @param name the display name
/// @param birthDate the birth date, or `null` if unknown
/// @param deathDate the death date, or `null` if unknown
/// @param gender the gender
/// @param biography the synopsis, or `null` if unavailable
public record PersonView(
    Long id,
    Long version,
    String name,
    LocalDate birthDate,
    LocalDate deathDate,
    Gender gender,
    String biography) {}
