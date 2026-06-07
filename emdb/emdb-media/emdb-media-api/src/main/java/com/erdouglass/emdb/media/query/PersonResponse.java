package com.erdouglass.emdb.media.query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import org.eclipse.microprofile.graphql.Ignore;
import org.eclipse.microprofile.graphql.Name;

import com.erdouglass.common.validation.DateRange;
import com.erdouglass.emdb.media.Gender;
import com.erdouglass.emdb.media.PersonConstants;
import com.erdouglass.emdb.media.ValidImage;
import com.erdouglass.emdb.media.show.ShowConstants;

public record PersonResponse(    
    @NotNull @Positive Long id,
    @NotNull @Positive Integer tmdbId,
    @NotBlank @Size(max = PersonConstants.NAME_MAX_LENGTH) String name,
    @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE) LocalDate birthDate,
    @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE) LocalDate deathDate,
    @NotNull Gender gender,
    @ValidImage String profile,
    @Size(max = PersonConstants.BIRTH_PLACE_MAX_LENGTH) String birthPlace,
    @Size(max = PersonConstants.BIOGRAPHY_MAX_LENGTH) String biography,
    @Valid @Ignore Credits credits) {
  
  @Override
  public String toString() {
    return getClass().getSimpleName() + "[id=" + id
        + ", tmdbId=" + tmdbId
        + ", name=" + name
        + ", birthDate=" + birthDate
        + "]";
  }
  
  @Name("PersonCredits")
  public record Credits(List<@Valid CastCredit> cast, List<@Valid CrewCredit> crew) {}
  
  @Name("PersonCastCredit")
  public record CastCredit(
      @NotNull UUID creditId,
      @NotNull @Positive Long id,
      @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
      @DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate releaseDate,
      @DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate firstAirDate,
      @NotNull @DecimalMin("0") @DecimalMax("10") Float score,
      @ValidImage String backdrop,
      @ValidImage String poster,
      @Size(max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview,
      @Size(max = ShowConstants.ROLE_MAX_LENGTH) String character, 
      List<@Valid Role> roles,
      @NotNull MediaType type) {}
  
  @Name("PersonCrewCredit")
  public record CrewCredit(
      @NotNull UUID creditId,
      @NotNull @Positive Long id,
      @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
      @DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate releaseDate,
      @DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate firstAirDate,
      @NotNull @DecimalMin("0") @DecimalMax("10") Float score,
      @ValidImage String backdrop,
      @ValidImage String poster,
      @Size(max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview,
      @Size(max = ShowConstants.ROLE_MAX_LENGTH) String job, 
      List<@Valid Job> jobs,
      @NotNull MediaType type) {}
}
