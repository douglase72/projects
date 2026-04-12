package com.erdouglass.emdb.common.comand;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.Gender;
import com.erdouglass.emdb.common.Image;
import com.erdouglass.emdb.common.PersonConstants;
import com.erdouglass.validation.DateRange;

/// Command to create or update a person, matched by TMDB ID.
///
/// If no person exists with the given [#tmdbId()], a new one is created.
/// Otherwise, the existing person is updated with the provided fields.
public record SavePerson(
    @NotNull @Positive Integer tmdbId,
    @NotBlank @Size(max = PersonConstants.NAME_MAX_LENGTH) String name,
    @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE) LocalDate birthDate,
    @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE) LocalDate deathDate,
    Gender gender,
    Image profile,
    @Size(min = 1, max = Configuration.URL_MAX_LENGTH) String homepage,
    @Size(max = PersonConstants.BIRTH_PLACE_MAX_LENGTH) String birthPlace,
    @Size(max = PersonConstants.BIOGRAPHY_MAX_LENGTH) String biography) {
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder extends AbstractPersonBuilder<Builder> {
    private Image profile;
    
    private Builder() { }

    public SavePerson build() {
      return new SavePerson(
            tmdbId,
            name, 
            birthDate,
            deathDate,
            gender,
            profile,
            homepage,
            birthPlace,
            biography);
    }
    
    public Builder profile(Image profile) {
      this.profile = profile;
      return self();
    }

    @Override
    protected Builder self() {
      return this;
    }
  }  
}
