package com.erdouglass.emdb.ingest.core.movie;

import java.time.LocalDate;
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

import com.erdouglass.common.validation.DateRange;
import com.erdouglass.emdb.media.MediaConstants;
import com.erdouglass.emdb.media.person.PersonConstants;
import com.erdouglass.emdb.media.show.ShowConstants;
import com.erdouglass.emdb.media.show.ShowStatus;

record TmdbMovie(    
    @NotNull @Positive Integer id,
    @NotBlank @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
    @DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate release_date,
    @NotNull @DecimalMin("0") @DecimalMax("10") Float vote_average,
    @NotNull ShowStatus status,
    @PositiveOrZero Integer runtime,
    @PositiveOrZero Integer budget,
    @PositiveOrZero Integer revenue,
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String backdrop_path,
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String poster_path,
    @Size(min = 1, max = MediaConstants.URL_MAX_LENGTH) String homepage,
    @NotBlank @Size(min = MediaConstants.ISO_639_1_LENGTH, max = MediaConstants.ISO_639_1_LENGTH) String original_language,
    @Size(max = ShowConstants.TAGLINE_MAX_LENGTH) String tagline,
    @Size(min = 1, max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview,
    @Valid Credits credits) {

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[id=" + id
        + ", title=" + title
        + ", release_date" + release_date
        + "]";
  }
  
  public record Credits(List<@Valid CastCredit> cast, List<@Valid CrewCredit> crew) {}

  public record CastCredit (
      @NotBlank String credit_id,
      @NotNull @Positive Integer id,
      @NotBlank @Size(max = PersonConstants.NAME_MAX_LENGTH) String name,
      @NotNull @Min(0) @Max(3) Integer gender,
      @Size(min = PersonConstants.PROFILE_MIN_LENGTH, max = PersonConstants.PROFILE_MAX_LENGTH) String profile_path,
      @Size(max = ShowConstants.ROLE_MAX_LENGTH) String character,
      @NotNull @PositiveOrZero Integer order) {}

  public record CrewCredit (
      @NotBlank String credit_id,
      @NotNull @Positive Integer id,
      @NotBlank @Size(max = PersonConstants.NAME_MAX_LENGTH) String name,
      @NotNull @Min(0) @Max(3) Integer gender,
      @Size(min = PersonConstants.PROFILE_MIN_LENGTH, max = PersonConstants.PROFILE_MAX_LENGTH) String profile_path,
      @Size(max = ShowConstants.ROLE_MAX_LENGTH) String job) {}    
}
