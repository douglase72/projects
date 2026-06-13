package com.erdouglass.emdb.ingest.scraper.series;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.media.PersonConstants;
import com.erdouglass.emdb.media.show.SeriesType;
import com.erdouglass.emdb.media.show.ShowConstants;
import com.erdouglass.emdb.media.show.ShowStatus;

record Series(
    @NotNull @Positive Integer id,
    @NotBlank @Size(max = ShowConstants.TITLE_MAX_LENGTH) String name,
    @NotNull @DecimalMin("0") @DecimalMax("10") Float vote_average,
    @NotNull ShowStatus status,
    @NotNull SeriesType type,
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String backdrop_path,
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String poster_path,
    @Size(min = 1, max = Configuration.URL_MAX_LENGTH) String homepage,
    @NotBlank @Size(min = Configuration.ISO_639_1_LENGTH, max = Configuration.ISO_639_1_LENGTH) String original_language,
    @Size(max = ShowConstants.TAGLINE_MAX_LENGTH) String tagline,
    @Size(min = 1, max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview,
    @NotNull @Valid Credits aggregate_credits) {
  
  public record Credits(List<@Valid TmdbCastCredit> cast, List<@Valid TmdbCrewCredit> crew) {}
  
  public record TmdbCastCredit (
      @NotNull @Positive Integer id,
      @NotBlank @Size(max = PersonConstants.NAME_MAX_LENGTH) String name,
      @NotNull @Min(0) @Max(3) Integer gender,
      @Size(min = PersonConstants.PROFILE_MIN_LENGTH, max = PersonConstants.PROFILE_MAX_LENGTH) String profile_path,
      List<@Valid Role> roles,
      @NotNull @PositiveOrZero Integer total_episode_count,
      @NotNull @PositiveOrZero Integer order) {

    public record Role(
        @NotBlank String credit_id,
        @Size(max = ShowConstants.ROLE_MAX_LENGTH) String character,
        @NotNull @PositiveOrZero Integer episode_count) {}
  }
  
  public record TmdbCrewCredit(
      @NotNull @Positive Integer id,
      @NotBlank @Size(max = PersonConstants.NAME_MAX_LENGTH) String name,
      @NotNull @Min(0) @Max(3) Integer gender,
      @Size(min = PersonConstants.PROFILE_MIN_LENGTH, max = PersonConstants.PROFILE_MAX_LENGTH) String profile_path,
      List<@Valid Job> jobs,
      @NotNull @PositiveOrZero Integer total_episode_count) {

    public record Job(
        @NotBlank String credit_id,
        @Size(max = ShowConstants.ROLE_MAX_LENGTH) String job,
        @NotNull @PositiveOrZero Integer episode_count) {}
  }
}
