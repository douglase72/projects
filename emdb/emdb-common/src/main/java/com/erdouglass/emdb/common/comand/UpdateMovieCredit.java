package com.erdouglass.emdb.common.comand;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.CreditType;
import com.erdouglass.emdb.common.ShowConstants;

public record UpdateMovieCredit(
    UUID id,
    @NotNull Long personId,
    @NotNull CreditType type,
    @Size(max = ShowConstants.ROLE_MAX_LENGTH) String role,
    @PositiveOrZero Integer order) {
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private UUID id;
    private Long personId;
    private Integer order;
    private String role;
    private CreditType type;
    
    private Builder() { }
    
    public UpdateMovieCredit build() {
      return new UpdateMovieCredit(id, personId, type, role, order);
    }
    
    public Builder id(UUID id) {
      this.id = id;
      return this;
    }  
    
    public Builder order(Integer order) {
      this.order = order;
      return this;
    }
    
    public Builder personId(Long personId) {
      this.personId = personId;
      return this;
    }
    
    public Builder role(String role) {
      this.role = role;
      return this;
    }
    
    public Builder type(CreditType type) {
      this.type = type;
      return this;
    }
  }  

}
