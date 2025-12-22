package com.erdouglass.emdb.common.request;

import java.time.LocalDate;
import java.util.Optional;

import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.Gender;
import com.erdouglass.emdb.common.PersonConstants;
import com.erdouglass.validation.DateRange;

public record PersonUpdateRequest(
    Optional<@Size(max = PersonConstants.NAME_MAX_LENGTH) String> name,
    Optional<@DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE) LocalDate> birthDate,
    Optional<@DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE) LocalDate> deathDate,
    Optional<Gender> gender,
    Optional<@Size(max = PersonConstants.BIRTH_PLACE_MAX_LENGTH) String> birthPlace,
    Optional<@Size(min = PersonConstants.PROFILE_MIN_LENGTH, max = PersonConstants.PROFILE_MAX_LENGTH) String> profile,
    Optional<@Size(max = PersonConstants.BIOGRAPHY_MAX_LENGTH) String> biography) {
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private Optional<String> biography;
    private Optional<LocalDate> birthDate;
    private Optional<String> birthPlace;
    private Optional<LocalDate> deathDate;
    private Optional<Gender> gender;
    private Optional<String> name;
    private Optional<String> profile;
    
    private Builder() { }
    
    public PersonUpdateRequest build() {
      return new PersonUpdateRequest(
            name, 
            birthDate, 
            deathDate, 
            gender,
            birthPlace, 
            profile, 
            biography);
    }
    
    public Builder biography(String biography) {
      this.biography = Optional.ofNullable(biography);
      return this;
    }
    
    public Builder birthDate(LocalDate birthDate) {
      this.birthDate = Optional.ofNullable(birthDate);
      return this;
    }
    
    public Builder birthPlace(String birthPlace) {
      this.birthPlace = Optional.ofNullable(birthPlace);
      return this;
    }
    
    public Builder deathDate(LocalDate deathDate) {
      this.deathDate = Optional.ofNullable(deathDate);
      return this;
    }
    
    public Builder gender(Gender gender) {
      this.gender = Optional.ofNullable(gender);
      return this;
    }
    
    public Builder name(String name) {
      this.name = Optional.ofNullable(name);
      return this;
    }
    
    public Builder profile(String profile) {
      this.profile = Optional.ofNullable(profile);
      return this;
    }
  }

}
