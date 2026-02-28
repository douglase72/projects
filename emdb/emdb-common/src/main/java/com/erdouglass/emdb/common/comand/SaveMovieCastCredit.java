package com.erdouglass.emdb.common.comand;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.ShowConstants;

public record SaveMovieCastCredit(
    @Size(max = ShowConstants.ROLE_MAX_LENGTH) String character,
    @NotNull @Valid SavePerson person,
    @PositiveOrZero Integer order) {
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private Integer order;
    private SavePerson person;
    private String character;
    
    private Builder() { }
    
    public SaveMovieCastCredit build() {
      return new SaveMovieCastCredit(character, person, order);
    }
    
    public Builder order(Integer order) {
      this.order = order;
      return this;
    }    
    
    public Builder person(SavePerson person) {
      this.person = person;
      return this;
    }
    
    public Builder character(String character) {
      this.character = character;
      return this;
    }
  }   

}
