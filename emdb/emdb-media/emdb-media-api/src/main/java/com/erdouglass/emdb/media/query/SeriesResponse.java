package com.erdouglass.emdb.media.query;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import org.eclipse.microprofile.graphql.Ignore;
import org.eclipse.microprofile.graphql.Name;

import com.erdouglass.common.validation.DateRange;
import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.media.Gender;
import com.erdouglass.emdb.media.PersonConstants;
import com.erdouglass.emdb.media.ValidImage;
import com.erdouglass.emdb.media.show.SeriesType;
import com.erdouglass.emdb.media.show.ShowConstants;
import com.erdouglass.emdb.media.show.ShowStatus;

public record SeriesResponse(
    @NotNull @Positive Long id,
    @NotNull @Positive Integer tmdbId,
    @NotBlank @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
    @DateRange(min = ShowConstants.SERIES_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate firstAirDate,
    @DateRange(min = ShowConstants.SERIES_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate lastAirDate,    
    @NotNull @DecimalMin("0") @DecimalMax("10") Float score,
    @NotNull ShowStatus status,
    @NotNull SeriesType type,
    @ValidImage String backdrop,
    @ValidImage String poster,
    @Size(min = 1, max = Configuration.URL_MAX_LENGTH) String homepage,
    @Size(min = Configuration.ISO_639_1_LENGTH, max = Configuration.ISO_639_1_LENGTH) String originalLanguage,
    @Size(max = ShowConstants.TAGLINE_MAX_LENGTH) String tagline,
    @Size(min = 1, max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview,
    @Valid @Ignore Credits credits) {
  
  @Name("SeriesCredits")
  public record Credits(List<@Valid CastCredit> cast, List<@Valid CrewCredit> crew) {}
  
  @Name("SeriesCastCredit")
  public record CastCredit(
      @NotNull @Positive Long id,
      @NotBlank @Size(max = PersonConstants.NAME_MAX_LENGTH) String name,
      @NotNull Gender gender,
      @ValidImage String profile,      
      @NotEmpty List<@Valid Role> roles,
      @NotNull @PositiveOrZero Integer order) {}
  
  @Name("SeriesCrewCredit")
  public record CrewCredit(
      @NotNull @Positive Long id,
      @NotBlank @Size(max = PersonConstants.NAME_MAX_LENGTH) String name,
      @NotNull Gender gender,
      @ValidImage String profile,      
      @NotEmpty List<@Valid Job> jobs,
      @NotNull @PositiveOrZero Integer order) {} 
}
