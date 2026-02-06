package com.erdouglass.emdb.scraper.query;

import java.time.LocalDate;
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
import com.erdouglass.emdb.common.ShowConstants;
import com.erdouglass.emdb.common.ShowStatus;
import com.erdouglass.validation.DateRange;

public record TmdbMovieDto(
    @NotNull @Positive Integer id,
    @NotBlank @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
    @DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate release_date,
    @Min(0) @Max(10) Float vote_average, 
    ShowStatus status,
    @PositiveOrZero Integer runtime,
    @PositiveOrZero Integer budget,
    @PositiveOrZero Integer revenue,
    @Size(max = Configuration.URL_MAX_LENGTH) String homepage,
    @Size(min = Configuration.ISO_639_1_LENGTH, max = Configuration.ISO_639_1_LENGTH) String original_language,
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String backdrop_path,
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String poster_path,
    @Size(max = ShowConstants.TAGLINE_MAX_LENGTH) String tagline,
    @Size(max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview,
    @NotNull @Valid Credits credits) {
  
  public record Credits(List<@Valid CastCredit> cast, List<@Valid CrewCredit> crew) {

  }
  
  public record CastCredit (
      @NotBlank String credit_id,
      @NotNull @Positive Integer id,
      @NotBlank @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
      @NotNull @Min(0) @Max(3) Integer gender,
      @Size(min = PersonConstants.PROFILE_MIN_LENGTH, max = PersonConstants.PROFILE_MAX_LENGTH) String profile_path,
      @Size(max = ShowConstants.ROLE_MAX_LENGTH) String character,
      @NotNull @PositiveOrZero Integer order) {
    
  }
  
  public record CrewCredit (
      @NotBlank String credit_id,
      @NotNull @Positive Integer id,
      @NotBlank @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
      @NotNull @Min(0) @Max(3) Integer gender,
      @Size(min = PersonConstants.PROFILE_MIN_LENGTH, max = PersonConstants.PROFILE_MAX_LENGTH) String profile_path,
      @Size(max = ShowConstants.ROLE_MAX_LENGTH) String job) {
    
  } 
  
  @Override
  public String toString() {
    return "TmdbMovie[id=" + id 
        + ", title=" + title 
        + ", release_date=" + release_date 
        + "]";
  }

}
