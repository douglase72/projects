package com.erdouglass.emdb.common.command;

import java.util.ArrayList;
import java.util.List;

import com.erdouglass.emdb.common.CreditConstants;
import com.erdouglass.emdb.common.CreditType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record SeriesCreditCreateCommand(
    @NotNull CreditType creditType,
    @NotNull @Positive Integer id,
    @NotEmpty @Valid List<Role> roles,
    @PositiveOrZero Integer order) {

  public record Role(
      @NotBlank String tmdbId,
      @Size(max = CreditConstants.ROLE_MAX_LENGTH) String role,
      @NotNull @PositiveOrZero Integer episodeCount) {
    
    public static Role of(String tmdbId, String role, Integer episodeCount) {
      return new Role(tmdbId, role, episodeCount);
    }
  }
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private CreditType creditType;
    private Integer id;
    private List<Role> roles = new ArrayList<>();
    private Integer order;

    private Builder() {
    }

    public SeriesCreditCreateCommand build() {
      return new SeriesCreditCreateCommand(creditType, id, roles, order);
    }
    
    public Builder creditType(CreditType creditType) {
      this.creditType = creditType;
      return this;
    }

    public Builder order(Integer order) {
      this.order = order;
      return this;
    }

    public Builder personId(Integer personId) {
      this.id = personId;
      return this;
    }

    public Builder roles(List<Role> roles) {
      this.roles = new ArrayList<>(roles);
      return this;
    }
  }
  
}
