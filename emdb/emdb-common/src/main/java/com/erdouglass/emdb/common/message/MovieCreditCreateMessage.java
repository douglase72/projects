package com.erdouglass.emdb.common.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.CreditType;
import com.erdouglass.emdb.common.ShowConstants;

public record MovieCreditCreateMessage(
    @NotBlank String tmdbId,
    @NotNull CreditType creditType,
    @NotNull @Positive Integer id,
    @Size(max = ShowConstants.ROLE_MAX_LENGTH) String role,    
    @PositiveOrZero Integer order) {
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private CreditType creditType;
    private Integer order;
    private Integer id;
    private String role;
    private String tmdbId;
    
    private Builder() { }
    
    public MovieCreditCreateMessage build() {
      return new MovieCreditCreateMessage(tmdbId, creditType, id, role, order);
    }
    
    public Builder creditType(CreditType creditType) {
      this.creditType = creditType;
      return this;
    }
    
    public Builder order(Integer order) {
      this.order = order;
      return this;
    }    
    
    public Builder personId(Integer tmdbId) {
      this.id = tmdbId;
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
