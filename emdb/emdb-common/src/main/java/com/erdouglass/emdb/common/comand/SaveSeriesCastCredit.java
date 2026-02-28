package com.erdouglass.emdb.common.comand;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.ShowConstants;

public record SaveSeriesCastCredit(
    @NotNull @Valid SavePerson person,
    @NotEmpty List<@Valid Role> roles,
    @PositiveOrZero Integer order) {
  
  public record Role(
      @Size(max = ShowConstants.ROLE_MAX_LENGTH) String character,
      @NotNull @PositiveOrZero Integer episodeCount) {
    
    public static Role of(String character, Integer episodeCount) {
      return new Role(character, episodeCount);
    }
  }  
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private SavePerson person;
    private List<Role> roles = new ArrayList<>();
    private Integer order;

    private Builder() {
    }

    public SaveSeriesCastCredit build() {
      return new SaveSeriesCastCredit(person, roles, order);
    }

    public Builder order(Integer order) {
      this.order = order;
      return this;
    }

    public Builder person(SavePerson person) {
      this.person = person;
      return this;
    }
    
    public Builder role(String chaarcter, Integer episodeCount) {
      this.roles.add(new Role(chaarcter, episodeCount));
      return this;
    }

    public Builder roles(List<Role> roles) {
      this.roles = new ArrayList<>(roles);
      return this;
    }
  }  

}
