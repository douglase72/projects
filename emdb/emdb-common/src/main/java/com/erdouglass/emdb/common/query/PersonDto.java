package com.erdouglass.emdb.common.query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.Gender;
import com.erdouglass.emdb.common.MediaType;
import com.erdouglass.emdb.common.PersonConstants;
import com.erdouglass.emdb.common.ShowConstants;
import com.erdouglass.emdb.common.ValidImage;
import com.erdouglass.validation.DateRange;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public record PersonDto(    
    @NotNull @Positive Long id,
    @NotNull @Positive Integer tmdbId,
    @NotBlank @Size(max = PersonConstants.NAME_MAX_LENGTH) String name,
    @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE) LocalDate birthDate,
    @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE) LocalDate deathDate,
    Gender gender,
    @ValidImage String profile,
    @Size(max = PersonConstants.BIRTH_PLACE_MAX_LENGTH) String birthPlace,
    @Size(max = PersonConstants.BIOGRAPHY_MAX_LENGTH) String biography,
    @Valid Credits credits) {
  
  public record Credits(List<@Valid PersonCredit> cast, List<@Valid PersonCredit> crew) {}
  
  @JsonInclude(Include.NON_EMPTY)
  public record MovieCredit(
      @NotNull UUID creditId, 
      @NotNull @Positive Long id,
      @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
      @DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate releaseDate,
      @Min(0) @Max(10) Float score,
      @ValidImage String backdrop,
      @ValidImage String poster,
      @Size(max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview,
      @Size(max = ShowConstants.ROLE_MAX_LENGTH) String character,
      @Size(max = ShowConstants.ROLE_MAX_LENGTH) String job,      
      @NotNull MediaType type) implements PersonCredit {}
  
  @JsonInclude(Include.NON_EMPTY)
  public record SeriesCredit(
      @NotNull UUID creditId, 
      @NotNull @Positive Long id,
      @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
      @DateRange(min = ShowConstants.SERIES_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate firstAirDate,
      @Min(0) @Max(10) Float score,
      @ValidImage String backdrop,
      @ValidImage String poster,
      @Size(max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview,
      @NotNull List<@Valid Role> roles,
      @NotNull List<@Valid Job> jobs,
      @NotNull MediaType type) implements PersonCredit {}

}
