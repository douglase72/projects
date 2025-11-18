package com.erdouglass.emdb.scraper.dto;

import java.util.List;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.CreditConstants;
import com.erdouglass.emdb.common.PersonConstants;
import com.erdouglass.emdb.common.ShowConstants;
import com.erdouglass.emdb.common.ShowStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record TmdbSeries(
    @NotNull @Positive Integer id,
    @NotBlank @Size(max = ShowConstants.NAME_MAX_LENGTH) String name,
    @PositiveOrZero Float vote_average,
    @NotNull @Size(max = ShowConstants.STATUS_MAX_LENGTH) ShowStatus status,
    @Size(max = ShowConstants.SERIES_TYPE_MAX_LENGTH) String type,
    @Size(max = Configuration.URL_MAX_LENGTH) String homepage,
    @Size(min = Configuration.ISO_639_1_LENGTH, max = Configuration.ISO_639_1_LENGTH) String original_language,    
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String backdrop_path,
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String poster_path,
    @Size(max = ShowConstants.TAGLINE_MAX_LENGTH) String tagline,
    @Size(max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview,
    @NotNull @Valid Credits aggregate_credits) {
  
  public record Credits(@Valid List<CastCredit> cast, @Valid List<CrewCredit> crew) {
    
  }
  
  public record CastCredit (
      @NotNull @Positive Integer id,
      @NotBlank @Size(max = ShowConstants.NAME_MAX_LENGTH) String name,
      @NotNull @Min(0) @Max(3) Integer gender,
      @Size(min = PersonConstants.PROFILE_MIN_LENGTH, max = PersonConstants.PROFILE_MAX_LENGTH) String profile_path,
      @NotNull @Valid List<Role> roles,
      @NotNull @Positive Integer total_episode_count,
      @NotNull @PositiveOrZero Integer order) {

    public record Role(
        @NotBlank String credit_id,
        @Size(max = CreditConstants.ROLE_MAX_LENGTH) String character,
        @NotNull @Positive Integer episode_count) {

    }
  }
  
  public record CrewCredit(
      @NotNull @Positive Integer id,
      @NotBlank @Size(min = 1, max = ShowConstants.NAME_MAX_LENGTH) String name,
      @NotNull @Min(0) @Max(3) Integer gender,
      @Size(min = PersonConstants.PROFILE_MIN_LENGTH, max = PersonConstants.PROFILE_MAX_LENGTH) String profile_path,
      @NotNull @Valid List<Job> jobs,
      @NotNull @Positive Integer total_episode_count) {

    public record Job(
        @NotBlank String credit_id,
        @Size(max = CreditConstants.ROLE_MAX_LENGTH) String job,
        @NotNull @PositiveOrZero Integer episode_count) {

    }
  }
  
  @Override
  public String toString() {
    return "TmdbSeries[id=" + id 
        + ", name=" + name 
        + ", status=" + status 
        + "]";
  }

}
