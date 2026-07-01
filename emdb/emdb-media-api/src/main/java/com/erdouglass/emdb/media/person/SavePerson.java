package com.erdouglass.emdb.media.person;

import java.time.LocalDate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.erdouglass.common.validation.DateRange;
import com.erdouglass.emdb.media.MediaConstants;
import com.erdouglass.emdb.media.image.Image;

public record SavePerson(    
    @NotNull @Positive Integer tmdbId,
    @NotBlank @Size(max = PersonConstants.NAME_MAX_LENGTH) String name,
    @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE) LocalDate birthDate,
    @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE) LocalDate deathDate,
    @NotNull Gender gender,
    @Valid Image profile,
    @Size(max = MediaConstants.URL_MAX_LENGTH) String homepage,
    @Size(max = PersonConstants.BIRTH_PLACE_MAX_LENGTH) String birthPlace,
    @Size(max = PersonConstants.BIOGRAPHY_MAX_LENGTH) String biography) {

  public static Builder builder() {
    return new Builder();
  }
  
  public static Builder builder(SavePerson command) {
    return builder()
        .tmdbId(command.tmdbId)
        .name(command.name)
        .birthDate(command.birthDate)
        .deathDate(command.deathDate)
        .gender(command.gender)
        .profile(command.profile)
        .homepage(command.homepage)
        .birthPlace(command.birthPlace)
        .biography(command.biography);
  }
  
  @Override
  public String toString() {
    return "SavePerson[tmdbId=" + tmdbId
        + ", name=" + name
        + ", birthDate=" + birthDate
        + "]";
  }
  
  public static final class Builder extends PersonBuilder<Builder> {
    private Integer tmdbId;
    
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
    
    public Builder tmdbId(Integer tmdbId) {
      this.tmdbId = tmdbId;
      return this;
    }

    @Override
    protected Builder self() {
      return this;
    }
  }  
}
