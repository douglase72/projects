package com.erdouglass.emdb.common.query;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.EmdbImage;
import com.erdouglass.emdb.common.Gender;
import com.erdouglass.emdb.common.PersonConstants;
import com.erdouglass.emdb.common.ShowConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public record MovieCreditDto(
    @NotNull UUID creditId, 
    @NotNull @Positive Long id,
    @NotBlank @Size(max = PersonConstants.NAME_MAX_LENGTH) String name, 
    @NotNull Gender gender,
    @EmdbImage String profile, 
    @Size(max = ShowConstants.ROLE_MAX_LENGTH) String character,
    @Size(max = ShowConstants.ROLE_MAX_LENGTH) String job,
    @PositiveOrZero Integer order) {
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private String character;
    private UUID creditId;
    private Gender gender;
    private Long id;
    private String job;
    private String name;
    private Integer order;
    private String profile;

    private Builder() { }
    
    public MovieCreditDto build() {
      return new MovieCreditDto(
            creditId,
            id, 
            name, 
            gender, 
            profile, 
            character,
            job,
            order);
    }
    
    public Builder character(String character) {
      this.character = character;
      return this;
    }
    
    public Builder creditId(UUID creditId) {
      this.creditId = creditId;
      return this;
    }
    
    public Builder gender(Gender gender) {
      this.gender = gender;
      return this;
    }
    
    public Builder id(Long id) {
      this.id = id;
      return this;
    }
    
    public Builder job(String job) {
      this.job = job;
      return this;
    }
    
    public Builder name(String name) {
      this.name = name;
      return this;
    }
    
    public Builder order(Integer order) {
      this.order = order;
      return this;
    }
    
    public Builder profile(String profile) {
      this.profile = profile;
      return this;
    }
  }   

}
