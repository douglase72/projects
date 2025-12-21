package com.erdouglass.emdb.common.message;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.Gender;
import com.erdouglass.emdb.common.PersonConstants;
import com.erdouglass.validation.DateRange;

public record PersonCreateMessage(
    @NotNull @Positive Integer tmdbId,
    @NotBlank @Size(max = PersonConstants.NAME_MAX_LENGTH) String name,
    @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE) LocalDate birthDate,
    @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE) LocalDate deathDate,
    @NotNull Gender gender,
    @Size(max = Configuration.URL_MAX_LENGTH) String homepage,
    @Size(max = PersonConstants.BIRTH_PLACE_MAX_LENGTH) String birthPlace,
    @Size(min = PersonConstants.PROFILE_MIN_LENGTH, max = PersonConstants.PROFILE_MAX_LENGTH) String profile,
    @Size(max = PersonConstants.BIOGRAPHY_MAX_LENGTH) String biography,
    @NotNull List<String> aliases) {
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private List<String> aliases = new ArrayList<>();
    private String biography;
    private LocalDate birthDate;
    private String birthPlace;
    private LocalDate deathDate;
    private Gender gender;
    private String homepage;
    private String name;
    private String profile;
    private Integer tmdbId;
    
    private Builder() { }
    
    public PersonCreateMessage build() {
      return new PersonCreateMessage(
            tmdbId,
            name, 
            birthDate, 
            deathDate, 
            gender,
            homepage,
            birthPlace, 
            profile, 
            biography,
            aliases);
    }
    
    public Builder aliases(List<String> aliases) {
      this.aliases = new ArrayList<>(aliases);
      return this;
    }
    
    public Builder biography(String biography) {
      this.biography = biography;
      return this;
    }
    
    public Builder birthDate(LocalDate birthDate) {
      this.birthDate = birthDate;
      return this;
    }
    
    public Builder birthPlace(String birthPlace) {
      this.birthPlace = birthPlace;
      return this;
    }
    
    public Builder deathDate(LocalDate deathDate) {
      this.deathDate = deathDate;
      return this;
    }
    
    public Builder gender(Gender gender) {
      this.gender = gender;
      return this;
    }
    
    public Builder homepage(String homepage) {
      this.homepage = homepage;
      return this;
    }
    
    public Builder name(String name) {
      this.name = name;
      return this;
    }
    
    public Builder profile(String profile) {
      this.profile = profile;
      return this;
    }
    
    public Builder tmdbId(Integer tmdbId) {
      this.tmdbId = tmdbId;
      return this;
    }
  } 

}
