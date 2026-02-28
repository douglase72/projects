package com.erdouglass.emdb.common.query;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.EmdbImage;
import com.erdouglass.emdb.common.Gender;
import com.erdouglass.emdb.common.PersonConstants;

public record SeriesCastCreditDto(
    @NotNull UUID creditId, 
    @NotNull @Positive Long id,
    @NotBlank @Size(max = PersonConstants.NAME_MAX_LENGTH) String name, 
    @NotNull Gender gender,
    @EmdbImage String profile, 
    List<@Valid RoleDto> roles,
    @NotNull @PositiveOrZero Integer totalEpisodes,
    @PositiveOrZero Integer order) {
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private List<RoleDto> roles = new ArrayList<>();
    private UUID creditId;
    private Gender gender;
    private Long id;
    private String name;
    private Integer order;
    private String profile;
    private Integer totalEpisodes;

    private Builder() { }
    
    public SeriesCastCreditDto build() {
      return new SeriesCastCreditDto(
            creditId,
            id, 
            name, 
            gender, 
            profile, 
            roles,
            totalEpisodes,
            order);
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
    
    public Builder roles(List<RoleDto> roles) {
      this.roles = new ArrayList<>(roles);
      return this;
    }
    
    public Builder totalEpisodes(Integer totalEpisodes) {
      this.totalEpisodes = totalEpisodes;
      return this;
    }
  }   

}
