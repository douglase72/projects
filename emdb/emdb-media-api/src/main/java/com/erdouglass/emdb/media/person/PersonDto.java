package com.erdouglass.emdb.media.person;

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
import org.eclipse.microprofile.graphql.NonNull;

import com.erdouglass.common.validation.DateRange;
import com.erdouglass.emdb.media.MediaConstants;
import com.erdouglass.emdb.media.MediaType;
import com.erdouglass.emdb.media.image.ValidImage;
import com.erdouglass.emdb.media.series.Job;
import com.erdouglass.emdb.media.series.Role;
import com.erdouglass.emdb.media.show.ShowConstants;

import io.smallrye.graphql.api.Union;

public record PersonDto(    
    @NotNull @Positive Long id,
    @NotNull @Positive Integer tmdbId,
    @NotBlank @Size(max = PersonConstants.NAME_MAX_LENGTH) String name,
    @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE) LocalDate birthDate,
    @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE) LocalDate deathDate,
    @NotNull Gender gender,
    @ValidImage String profile,
    @Size(max = MediaConstants.URL_MAX_LENGTH) String homepage,
    @Size(max = PersonConstants.BIRTH_PLACE_MAX_LENGTH) String birthPlace,
    @Size(max = PersonConstants.BIOGRAPHY_MAX_LENGTH) String biography,
    @Ignore @Valid PersonCredits credits) {

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[id=" + id
        + ", tmdbId=" + tmdbId
        + ", name=" + name
        + ", birthDate=" + birthDate
        + "]";
  }
  
  public record PersonCredits(
      @NotNull List<@NonNull PersonCastCredit> cast, 
      @NotNull List<@NonNull PersonCrewCredit> crew) {}
  
  @Union
  public sealed interface PersonCastCredit permits PersonMovieCastCredit, PersonSeriesCastCredit {
    @NotNull UUID creditId();
    @NotNull Long id();
    @NotNull String title();
    @NotNull Float score();
    String backdrop();
    String poster();
    String overview();
    @NotNull MediaType type();
  }
  
  @Union
  public sealed interface PersonCrewCredit permits PersonMovieCrewCredit, PersonSeriesCrewCredit {
    @NotNull UUID creditId();
    @NotNull Long id();
    @NotNull String title();
    @NotNull Float score();
    String backdrop();
    String poster();
    String overview();
    @NotNull MediaType type();
  }
  
  public record PersonMovieCastCredit(
      @NotNull UUID creditId,
      @NotNull @Positive Long id,
      @NotBlank @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
      @DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate releaseDate,
      @NotNull @DecimalMin("0") @DecimalMax("10") Float score,
      @ValidImage String backdrop,
      @ValidImage String poster,
      @Size(max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview,
      @Size(max = ShowConstants.ROLE_MAX_LENGTH) String character, 
      @NotNull MediaType type) implements PersonCastCredit {}
  
  public record PersonMovieCrewCredit(
      @NotNull UUID creditId,
      @NotNull @Positive Long id,
      @NotBlank @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
      @DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate releaseDate,
      @NotNull @DecimalMin("0") @DecimalMax("10") Float score,
      @ValidImage String backdrop,
      @ValidImage String poster,
      @Size(max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview,
      @Size(max = ShowConstants.ROLE_MAX_LENGTH) String job, 
      @NotNull MediaType type) implements PersonCrewCredit {} 
  
  public record PersonSeriesCastCredit(
      @NotNull UUID creditId,
      @NotNull @Positive Long id,
      @NotBlank @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
      @DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate firstAirDate,
      @NotNull @DecimalMin("0") @DecimalMax("10") Float score,
      @ValidImage String backdrop,
      @ValidImage String poster,
      @Size(max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview,
      List<@Valid Role> roles,
      @NotNull MediaType type) implements PersonCastCredit {}
  
  public record PersonSeriesCrewCredit(
      @NotNull UUID creditId,
      @NotNull @Positive Long id,
      @NotBlank @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
      @DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate firstAirDate,
      @NotNull @DecimalMin("0") @DecimalMax("10") Float score,
      @ValidImage String backdrop,
      @ValidImage String poster,
      @Size(max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview,
      List<@Valid Job> jobs,
      @NotNull MediaType type) implements PersonCrewCredit {}
}
