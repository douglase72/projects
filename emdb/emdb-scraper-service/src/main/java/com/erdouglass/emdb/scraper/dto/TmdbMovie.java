package com.erdouglass.emdb.scraper.dto;

import java.time.LocalDate;
import java.util.List;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.CreditConstants;
import com.erdouglass.emdb.common.PersonConstants;
import com.erdouglass.emdb.common.ShowConstants;
import com.erdouglass.emdb.common.ShowStatus;
import com.erdouglass.validation.DateRange;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record TmdbMovie(
    @NotNull @Positive Integer id,
    @NotBlank @Size(max = ShowConstants.NAME_MAX_LENGTH) String title,
    @DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate release_date,
    @Min(0) @Max(10) Float vote_average, 
    @NotNull ShowStatus status,
    @NotNull @PositiveOrZero Integer runtime,
    @NotNull @PositiveOrZero Integer budget,
    @NotNull @PositiveOrZero Integer revenue,
    @Size(max = Configuration.URL_MAX_LENGTH) String homepage,
    @Size(min = Configuration.ISO_639_1_LENGTH, max = Configuration.ISO_639_1_LENGTH) String original_language,
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String backdrop_path,
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String poster_path,
    @Size(max = ShowConstants.TAGLINE_MAX_LENGTH) String tagline,
    @Size(max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview,
    @NotNull @Valid Credits credits) {
  
  public record Credits(@Valid List<CastCredit> cast, @Valid List<CrewCredit> crew) {

  }
  
  public record CastCredit (
      @NotBlank String credit_id,
      @NotNull @Positive Integer id,
      @NotBlank @Size(max = ShowConstants.NAME_MAX_LENGTH) String name,
      @NotNull @Min(0) @Max(3) Integer gender,
      @Size(min = PersonConstants.PROFILE_MIN_LENGTH, max = PersonConstants.PROFILE_MAX_LENGTH) String profile_path,
      @Size(max = CreditConstants.ROLE_MAX_LENGTH) String character,
      @NotNull @PositiveOrZero Integer order) {
    
  }
  
  public record CrewCredit (
      @NotBlank String credit_id,
      @NotNull @Positive Integer id,
      @NotBlank @Size(max = ShowConstants.NAME_MAX_LENGTH) String name,
      @NotNull @Min(0) @Max(3) Integer gender,
      @Size(min = PersonConstants.PROFILE_MIN_LENGTH, max = PersonConstants.PROFILE_MAX_LENGTH) String profile_path,
      @Size(max = CreditConstants.ROLE_MAX_LENGTH) String job) {
    
  }

  @Override
  public String toString() {
    return "TmdbMovie[id=" + id 
        + ", title=" + title 
        + ", release_date=" + release_date 
        + "]";
  }

}
