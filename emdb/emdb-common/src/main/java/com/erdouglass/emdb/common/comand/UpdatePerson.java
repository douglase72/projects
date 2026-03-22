package com.erdouglass.emdb.common.comand;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.AbstractPersonBuilder;
import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.Gender;
import com.erdouglass.emdb.common.PersonConstants;
import com.erdouglass.validation.DateRange;

public record UpdatePerson(
    @NotBlank @Size(max = PersonConstants.NAME_MAX_LENGTH) String name,
    @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE) LocalDate birthDate,
    @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE) LocalDate deathDate,
    Gender gender,
    UUID profile,
    @Size(min = 1, max = Configuration.URL_MAX_LENGTH) String homepage,
    @Size(max = PersonConstants.BIRTH_PLACE_MAX_LENGTH) String birthPlace,
    @Size(max = PersonConstants.BIOGRAPHY_MAX_LENGTH) String biography) {

  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder extends AbstractPersonBuilder<Builder> {
    private UUID profile;
    
    private Builder() { }

    public UpdatePerson build() {
      return new UpdatePerson(
            name, 
            birthDate,
            deathDate,
            gender,
            profile,
            homepage,
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
