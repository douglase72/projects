package com.erdouglass.emdb.media.query;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import org.eclipse.microprofile.graphql.Ignore;
import org.eclipse.microprofile.graphql.Name;

import com.erdouglass.common.validation.DateRange;
import com.erdouglass.emdb.media.Gender;
import com.erdouglass.emdb.media.PersonConstants;
import com.erdouglass.emdb.media.ValidImage;

public record PersonResponse(    
    @NotNull @Positive Long id,
    @NotNull @Positive Integer tmdbId,
    @NotBlank @Size(max = PersonConstants.NAME_MAX_LENGTH) String name,
    @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE) LocalDate birthDate,
    @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE) LocalDate deathDate,
    @NotNull Gender gender,
    @ValidImage String profile,
    @Size(max = PersonConstants.BIRTH_PLACE_MAX_LENGTH) String birthPlace,
    @Size(max = PersonConstants.BIOGRAPHY_MAX_LENGTH) String biography,
    @Valid @Ignore Credits credits) {
  
  @Override
  public String toString() {
    return getClass().getSimpleName() + "[id=" + id
        + ", tmdbId=" + tmdbId
        + ", name=" + name
        + ", birthDate=" + birthDate
        + "]";
  }
  
  @Name("PersonCredits")
  public record Credits(List<@Valid PersonCastCredit> cast, List<@Valid PersonCrewCredit> crew) {}
}
