package com.erdouglass.emdb.ingest.core.person;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.erdouglass.common.validation.DateRange;
import com.erdouglass.emdb.media.MediaConstants;
import com.erdouglass.emdb.media.person.PersonConstants;

record TmdbPerson(
    @NotNull @Positive Integer id,
    @NotBlank@Size(max = PersonConstants.NAME_MAX_LENGTH) String name,
    @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE) LocalDate birthday,
    @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE) LocalDate deathday,
    @NotNull @Min(0) @Max(3) Integer gender,
    @Size(min = PersonConstants.PROFILE_MIN_LENGTH, max = PersonConstants.PROFILE_MAX_LENGTH) String profile_path,
    @Size(min = 1, max = MediaConstants.URL_MAX_LENGTH) String homepage,
    @Size(max = PersonConstants.BIRTH_PLACE_MAX_LENGTH) String place_of_birth,
    @Size(max = PersonConstants.BIOGRAPHY_MAX_LENGTH) String biography) {

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[id=" + id
        + ", name=" + name
        + ", birthday" + birthday
        + "]";
  }  
}
