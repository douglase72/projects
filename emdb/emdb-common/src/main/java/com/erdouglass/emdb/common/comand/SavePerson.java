package com.erdouglass.emdb.common.comand;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.AbstractPersonBuilder;
import com.erdouglass.emdb.common.Gender;
import com.erdouglass.emdb.common.PersonConstants;
import com.erdouglass.emdb.common.ShowConstants;
import com.erdouglass.validation.DateRange;

public record SavePerson(
    @NotNull @Positive Integer tmdbId,
    @Size(max = PersonConstants.NAME_MAX_LENGTH) String name,
    @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE) LocalDate birthDate,
    @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE) LocalDate deathDate,
    Gender gender,
    @Size(max = PersonConstants.BIRTH_PLACE_MAX_LENGTH) String birthPlace,
    UUID profile,
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String tmdbProfile,
    @Size(max = PersonConstants.BIOGRAPHY_MAX_LENGTH) String biography) {
  
  public static Builder builder() {
    return new Builder();
  }
  
  @Override
  public String toString() {
    return "SavePerson[tmdbId=" + tmdbId
            + ", name=" + name
            + "]";
  }
  
  public static final class Builder extends AbstractPersonBuilder<Builder> {
    private Integer tmdbId;
    private UUID profile;
    private String tmdbProfile;
    
    private Builder() { }

    public SavePerson build() {
      return new SavePerson(
            tmdbId,
            name, 
            birthDate,
            deathDate,
            gender,
            birthPlace,
            profile,
            tmdbProfile,
            biography);
    }
    
    public Builder profile(UUID profile) {
      this.profile = profile;
      return self();
    }
    
    public Builder tmdbId(Integer tmdbId) {
      this.tmdbId = tmdbId;
      return this;
    }
    
    public Builder tmdbProfile(String tmdbProfile) {
      this.tmdbProfile = tmdbProfile;
      return this;
    }  

    @Override
    protected Builder self() {
      return this;
    }
  }  

}
