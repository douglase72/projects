package com.erdouglass.emdb.scraper.query;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.PersonConstants;
import com.erdouglass.emdb.common.SeriesType;
import com.erdouglass.emdb.common.ShowConstants;
import com.erdouglass.emdb.common.ShowStatus;

public record TmdbSeriesDto(
    @NotNull @Positive Integer id,
    @NotBlank @Size(max = ShowConstants.TITLE_MAX_LENGTH) String name,
    @Min(0) @Max(10) Float vote_average, 
    ShowStatus status,
    SeriesType type,
    @Size(max = Configuration.URL_MAX_LENGTH) String homepage,
    @Size(min = Configuration.ISO_639_1_LENGTH, max = Configuration.ISO_639_1_LENGTH) String original_language,
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String backdrop_path,
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String poster_path,
    @Size(max = ShowConstants.TAGLINE_MAX_LENGTH) String tagline,
    @Size(max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview,
    @NotNull @Valid Credits aggregate_credits) {

  public record Credits(List<@Valid CastCredit> cast, List<@Valid CrewCredit> crew) {

  }
  
  public record CastCredit (
      @NotNull @Positive Integer id,
      @NotBlank @Size(max = PersonConstants.NAME_MAX_LENGTH) String name,
      @NotNull @Min(0) @Max(3) Integer gender,
      @Size(min = PersonConstants.PROFILE_MIN_LENGTH, max = PersonConstants.PROFILE_MAX_LENGTH) String profile_path,
      List<@Valid Role> roles,
      @NotNull @Positive Integer total_episode_count,
      @NotNull @PositiveOrZero Integer order) {

    public record Role(
        @NotBlank String credit_id,
        @Size(max = ShowConstants.ROLE_MAX_LENGTH) String character,
        @NotNull @Positive Integer episode_count) {

    }
  }
  
  public record CrewCredit(
      @NotNull @Positive Integer id,
      @NotBlank @Size(min = 1, max = PersonConstants.NAME_MAX_LENGTH) String name,
      @NotNull @Min(0) @Max(3) Integer gender,
      @Size(min = PersonConstants.PROFILE_MIN_LENGTH, max = PersonConstants.PROFILE_MAX_LENGTH) String profile_path,
      List<@Valid Job> jobs,
      @NotNull @Positive Integer total_episode_count) {

    public record Job(
        @NotBlank String credit_id,
        @Size(max = ShowConstants.ROLE_MAX_LENGTH) String job,
        @NotNull @PositiveOrZero Integer episode_count) {

    }
  }  
  
}
