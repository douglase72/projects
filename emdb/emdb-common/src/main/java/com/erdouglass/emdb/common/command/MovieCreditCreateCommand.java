package com.erdouglass.emdb.common.command;

import com.erdouglass.emdb.common.CreditConstants;
import com.erdouglass.emdb.common.CreditType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record MovieCreditCreateCommand(
    @NotNull CreditType creditType,
    @NotNull @Positive Long personId,
    @Size(max = CreditConstants.ROLE_MAX_LENGTH) String role,    
    @PositiveOrZero Integer order) {
	
  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private CreditType creditType;
    private Integer order;
    private Long personId;
    private String role;
    
    private Builder() { }
    
    public MovieCreditCreateCommand build() {
      return new MovieCreditCreateCommand(creditType, personId, role, order);
    }
    
    public Builder creditType(CreditType creditType) {
      this.creditType = creditType;
      return this;
    }
    
    public Builder order(Integer order) {
      this.order = order;
      return this;
    }    
    
    public Builder personId(long personId) {
      this.personId = personId;
      return this;
    }
    
    public Builder role(String role) {
      this.role = role;
      return this;
    }
  }

}
