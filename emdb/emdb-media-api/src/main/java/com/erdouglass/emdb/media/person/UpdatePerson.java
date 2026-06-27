package com.erdouglass.emdb.media.person;

import java.time.LocalDate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.erdouglass.common.validation.DateRange;
import com.erdouglass.emdb.media.MediaConstants;
import com.erdouglass.emdb.media.image.Image;

public record UpdatePerson(
    @NotBlank @Size(max = PersonConstants.NAME_MAX_LENGTH) String name,
    @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE) LocalDate birthDate,
    @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE) LocalDate deathDate,
    @NotNull Gender gender,
    @Valid Image profile,
    @Size(min = 1, max = MediaConstants.URL_MAX_LENGTH) String homepage,
    @Size(max = PersonConstants.BIRTH_PLACE_MAX_LENGTH) String birthPlace,
    @Size(max = PersonConstants.BIOGRAPHY_MAX_LENGTH) String biography) {

  public static Builder builder() {
    return new Builder();
  }
  
  public static Builder builder(UpdatePerson command) {
    return builder()
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
    return "SavePerson[name=" + name
        + ", birthDate=" + birthDate
        + "]";
  }
  
  public static final class Builder extends PersonBuilder<Builder> {
    
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

    @Override
    protected Builder self() {
      return this;
    }
  }      
}
