package com.erdouglass.emdb.common.query;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.EmdbImage;
import com.erdouglass.emdb.common.MediaType;
import com.erdouglass.emdb.common.ShowConstants;
import com.erdouglass.validation.DateRange;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public record PersonSeriesCreditDto(
    @NotNull UUID creditId, 
    @NotNull @Positive Long id,
    @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
    @NotEmpty List<@Valid RoleDto> roles,
    @NotEmpty List<@Valid JobDto> jobs,
    @DateRange(min = ShowConstants.SERIES_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate firstAirDate,
    @Min(0) @Max(10) Float score,
    @EmdbImage String backdrop,
    @EmdbImage String poster,
    @Size(max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview,
    @NotNull MediaType type) implements PersonCreditDto {
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private List<RoleDto> roles = new ArrayList<>();
    private List<JobDto> jobs = new ArrayList<>();
    private String backdrop;
    private UUID creditId;
    private Long id;
    private String overview;
    private String poster;
    private LocalDate releaseDate;
    private Float score;
    private String title;
    private MediaType type;
    
    private Builder() { }
    
    public PersonSeriesCreditDto build() {
      return new PersonSeriesCreditDto(
          creditId,
          id,
          title,
          roles,
          jobs,
          releaseDate,
          score,
          backdrop,
          poster,
          overview,
          type);
    }
    
    public Builder backdrop(String backdrop) {
      this.backdrop = backdrop;
      return this;
    }
    
    public Builder creditId(UUID creditId) {
      this.creditId = creditId;
      return this;
    }
    
    public Builder id(Long id) {
      this.id = id;
      return this;
    }
    
    public Builder jobs(List<JobDto> jobs) {
      this.jobs = new ArrayList<>(jobs);
      return this;
    }
    
    public Builder overview(String overview) {
      this.overview = overview;
      return this;
    }
    
    public Builder poster(String poster) {
      this.poster = poster;
      return this;
    }
    
    public Builder roles(List<RoleDto> roles) {
      this.roles = new ArrayList<>(roles);
      return this;
    }
    
    public Builder score(Float score) {
      this.score = score;
      return this;
    }
    
    public Builder title(String title) {
      this.title = title;
      return this;
    }
    
    public Builder type(MediaType type) {
      this.type = type;
      return this;
    }   
  }  

}
