package com.erdouglass.emdb.common;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record MovieCreditCreateDto(
    @NotBlank String tmdbId,
    @NotNull CreditType type,
    @Size(max = ShowConstants.ROLE_MAX_LENGTH) String role,
    @NotNull @Valid PersonCreateDto person,
    @PositiveOrZero Integer order) {
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private Integer order;
    private PersonCreateDto person;
    private String role;
    private String tmdbId;
    private CreditType type;
    
    private Builder() { }
    
    public MovieCreditCreateDto build() {
      return new MovieCreditCreateDto(tmdbId, type, role, person, order);
    }
    
    public Builder type(CreditType type) {
      this.type = type;
      return this;
    }
    
    public Builder order(Integer order) {
      this.order = order;
      return this;
    }    
    
    public Builder person(PersonCreateDto person) {
      this.person = person;
      return this;
    }
    
    public Builder role(String role) {
      this.role = role;
      return this;
    }
    
    public Builder tmdbId(String tmdbId) {
      this.tmdbId = tmdbId;
      return this;
    }
  }  

}
