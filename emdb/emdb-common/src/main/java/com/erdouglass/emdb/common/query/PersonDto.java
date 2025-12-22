package com.erdouglass.emdb.common.query;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.AbstractPersonBuilder;
import com.erdouglass.emdb.common.Gender;
import com.erdouglass.emdb.common.PersonConstants;
import com.erdouglass.validation.DateRange;

public record PersonDto(
    @NotNull @Positive Long id,
    @NotNull @Positive Integer tmdbId,
    @NotBlank @Size(max = PersonConstants.NAME_MAX_LENGTH) String name,
    @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE) LocalDate birthDate,
    @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE) LocalDate deathDate,
    @NotNull Gender gender,
    @Size(max = PersonConstants.BIRTH_PLACE_MAX_LENGTH) String birthPlace,
    @Size(min = PersonConstants.PROFILE_MIN_LENGTH, max = PersonConstants.PROFILE_MAX_LENGTH) String profile,
    @Size(max = PersonConstants.BIOGRAPHY_MAX_LENGTH) String biography) {
  
  public static Builder builder() {
    return new Builder();
  }
  
  @Override
  public String toString() {
    return "PersonDto[id=" + id
            + ", tmdbId=" + tmdbId
            + ", name=" + name
            + ", birthDate=" + birthDate
            + "]";
  }
  
  public static final class Builder extends AbstractPersonBuilder<Builder> {
    private Long id;
    
    private Builder() { }
    
    public PersonDto build() {
      return new PersonDto(
            id,
            tmdbId,
            name, 
            birthDate,
            deathDate,
            gender,
            birthPlace,
            profile,
            biography);
    }
    
    public Builder id(Long id) {
      this.id = id;
      return this;
    }
    
    @Override
    protected Builder self() {
      return this;
    }    
  }

}
