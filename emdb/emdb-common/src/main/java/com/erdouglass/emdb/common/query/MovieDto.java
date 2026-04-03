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
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.Gender;
import com.erdouglass.emdb.common.PersonConstants;
import com.erdouglass.emdb.common.ShowConstants;
import com.erdouglass.emdb.common.ShowStatus;
import com.erdouglass.emdb.common.ValidImage;
import com.erdouglass.validation.DateRange;

public record MovieDto(
    @NotNull @Positive Long id,
    @NotNull @Positive Integer tmdbId,
    @NotBlank @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
    @DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate releaseDate,
    @Min(0) @Max(10) Float score,
    ShowStatus status,
    @PositiveOrZero Integer runtime,
    @PositiveOrZero Integer budget,
    @PositiveOrZero Integer revenue,
    @ValidImage String backdrop,
    @ValidImage String poster,
    @Size(min = 1, max = Configuration.URL_MAX_LENGTH) String homepage,
    @Size(min = Configuration.ISO_639_1_LENGTH, max = Configuration.ISO_639_1_LENGTH) String originalLanguage,
    @Size(max = ShowConstants.TAGLINE_MAX_LENGTH) String tagline,
    @Size(min = 1, max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview,
    @Valid Credits credits) {
  
  public record Credits(List<@Valid CastCredit> cast, List<@Valid CrewCredit> crew) {
    
  }
  
  public record CastCredit(
      @NotNull UUID creditId, 
      @NotNull @Positive Long id,
      @NotBlank @Size(max = PersonConstants.NAME_MAX_LENGTH) String name, 
      @NotNull Gender gender,
      @ValidImage String profile, 
      @Size(max = ShowConstants.ROLE_MAX_LENGTH) String character,
      @PositiveOrZero Integer order) {
    
  }
  
  public record CrewCredit(
      @NotNull UUID creditId, 
      @NotNull @Positive Long id,
      @NotBlank @Size(max = PersonConstants.NAME_MAX_LENGTH) String name, 
      @NotNull Gender gender,
      @ValidImage String profile, 
      @Size(max = ShowConstants.ROLE_MAX_LENGTH) String job) {

  }

}
