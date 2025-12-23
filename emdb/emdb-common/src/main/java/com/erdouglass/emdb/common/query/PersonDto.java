package com.erdouglass.emdb.common.query;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
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
    @Size(max = PersonConstants.BIOGRAPHY_MAX_LENGTH) String biography,
    @Valid Credits credits) {
  
  public record Credits(List<@Valid PersonCreditDto> cast, List<@Valid PersonCreditDto> crew) {
    
  }
  
  public static Builder builder() {
    return new Builder();
  }
  
  public List<PersonCreditDto> cast() {
    return Optional.ofNullable(credits).map(c -> c.cast().stream().toList()).orElse(List.of());
  }
  
  public List<PersonCreditDto> crew() {
    return Optional.ofNullable(credits).map(c -> c.crew().stream().toList()).orElse(List.of());
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
    private List<PersonCreditDto> cast = new ArrayList<>();
    private List<PersonCreditDto> crew = new ArrayList<>();
    private Long id;
    
    private Builder() { }
    
    public PersonDto build() {
      Credits credits = null;
      if (!cast.isEmpty() || !crew.isEmpty()) {
        credits = new Credits(cast.stream().toList(), crew.stream().toList());
      }      
      return new PersonDto(
            id,
            tmdbId,
            name, 
            birthDate,
            deathDate,
            gender,
            birthPlace,
            profile,
            biography,
            credits);
    }
    
    public Builder cast(List<PersonCreditDto> cast) {
      this.cast = new ArrayList<>(cast);
      return this;
    }
    
    public Builder crew(List<PersonCreditDto> crew) {
      this.crew = new ArrayList<>(crew);
      return this;
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
