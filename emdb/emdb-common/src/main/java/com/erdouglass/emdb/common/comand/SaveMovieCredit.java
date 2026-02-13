package com.erdouglass.emdb.common.comand;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.CreditType;
import com.erdouglass.emdb.common.ShowConstants;

public record SaveMovieCredit(
    @NotNull CreditType type,
    @Size(max = ShowConstants.ROLE_MAX_LENGTH) String role,
    @NotNull @Valid SavePerson person,
    @PositiveOrZero Integer order) {
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private Integer order;
    private SavePerson person;
    private String role;
    private CreditType type;
    
    private Builder() { }
    
    public SaveMovieCredit build() {
      return new SaveMovieCredit(type, role, person, order);
    }
    
    public Builder type(CreditType type) {
      this.type = type;
      return this;
    }
    
    public Builder order(Integer order) {
      this.order = order;
      return this;
    }    
    
    public Builder person(SavePerson person) {
      this.person = person;
      return this;
    }
    
    public Builder role(String role) {
      this.role = role;
      return this;
    }
  }   

}
