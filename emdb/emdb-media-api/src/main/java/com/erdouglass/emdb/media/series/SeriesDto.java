package com.erdouglass.emdb.media.series;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import org.eclipse.microprofile.graphql.Ignore;
import org.eclipse.microprofile.graphql.NonNull;

import com.erdouglass.common.validation.DateRange;
import com.erdouglass.emdb.media.MediaConstants;
import com.erdouglass.emdb.media.image.ValidImage;
import com.erdouglass.emdb.media.person.Gender;
import com.erdouglass.emdb.media.person.PersonConstants;
import com.erdouglass.emdb.media.show.ShowConstants;
import com.erdouglass.emdb.media.show.ShowStatus;

public record SeriesDto(
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
    @Size(max = MediaConstants.URL_MAX_LENGTH) String homepage,
    @Size(min = MediaConstants.ISO_639_1_LENGTH, max = MediaConstants.ISO_639_1_LENGTH) String originalLanguage,
    @Size(max = ShowConstants.TAGLINE_MAX_LENGTH) String tagline,
    @Size(max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview,
    @Ignore @Valid SeriesCredits credits) {

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[id=" + id
        + ", tmdbId=" + tmdbId
        + ", title=" + title
        + ", firstAirDate=" + firstAirDate
        + ", lastAirDate=" + lastAirDate
        + "]";
  } 
  
  public record SeriesCredits(
      @NotNull List<@NonNull SeriesCastCredit> cast, 
      @NotNull List<@NonNull SeriesCrewCredit> crew) {}
  
  public record SeriesCastCredit(
      @NotNull @Positive Long id,
      @NotBlank @Size(max = PersonConstants.NAME_MAX_LENGTH) String name, 
      @NotNull Gender gender,
      @ValidImage String profile, 
      @NotNull List<@NonNull Role> roles,
      @NotNull @PositiveOrZero Integer totalEpisodes,
      @NotNull @PositiveOrZero Integer order) {}
  
  public record SeriesCrewCredit(
      @NotNull @Positive Long id,
      @NotBlank @Size(max = PersonConstants.NAME_MAX_LENGTH) String name, 
      @NotNull Gender gender,
      @ValidImage String profile, 
      @NotNull List<@NotNull Job> jobs,
      @NotNull @PositiveOrZero Integer totalEpisodes) {}
}
