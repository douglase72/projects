package com.erdouglass.emdb.media.person.adapter.in.rest;

import java.util.Optional;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.media.person.domain.Name;

public record SavePersonRequest(
    @NotBlank @Size(max = Name.MAX_LENGTH) String name,
    Optional<String> birthDate,
    Optional<String> deathDate,
    @NotBlank String gender,
    Optional<String> biography) {
  
  public static Builder builder() { return new Builder(); }
  
  public static final class Builder {
    private String name;
    private String birthDate;
    private String deathDate;
    private String gender;
    private String biography;
    
    private Builder() {}
    
    public SavePersonRequest build() {
      return new SavePersonRequest(
          name, 
          Optional.ofNullable(birthDate), 
          Optional.ofNullable(deathDate), 
          gender, 
          Optional.ofNullable(biography));
    }
    
    public Builder biography(String biography) {
      this.biography = biography;
      return this;
    }
    
    public Builder birthDate(String birthDate) {
      this.birthDate = birthDate;
      return this;
    }
    
    public Builder deathDate(String deathDate) {
      this.deathDate = deathDate;
      return this;
    }
    
    public Builder gender(String gender) {
      this.gender = gender;
      return this;
    }
    
    public Builder name(String name) {
      this.name = name;
      return this;
    }
  }
}
