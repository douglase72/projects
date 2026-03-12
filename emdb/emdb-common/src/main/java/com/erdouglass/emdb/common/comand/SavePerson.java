package com.erdouglass.emdb.common.comand;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.AbstractPersonBuilder;
import com.erdouglass.emdb.common.Gender;
import com.erdouglass.emdb.common.PersonConstants;
import com.erdouglass.validation.DateRange;

public record SavePerson(    
    @NotNull @Positive Integer tmdbId,
    @NotBlank @Size(max = PersonConstants.NAME_MAX_LENGTH) String name,
    @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE) LocalDate birthDate,
    @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE) LocalDate deathDate,
    Gender gender,
    UUID profile,
    @Size(max = PersonConstants.BIRTH_PLACE_MAX_LENGTH) String birthPlace,
    @Size(max = PersonConstants.BIOGRAPHY_MAX_LENGTH) String biography) {
  
  public static Builder builder() {
    return new Builder();
  }
  
  @Override
  public String toString() {
    return "SavePerson[tmdbId=" + tmdbId
            + ", name=" + name
            + ", birthDate=" + birthDate
            + ", deathDate=" + deathDate
            + "]";
  }
  
  public static final class Builder extends AbstractPersonBuilder<Builder> {
    private UUID profile;
    
    private Builder() { }

    public SavePerson build() {
      return new SavePerson(
            tmdbId,
            name, 
            birthDate,
            deathDate,
            gender,
            profile,
            birthPlace,
            biography);
    }
    
    public Builder profile(UUID profile) {
      this.profile = profile;
      return self();
    }

    @Override
    protected Builder self() {
      return this;
    }
  }    

}
