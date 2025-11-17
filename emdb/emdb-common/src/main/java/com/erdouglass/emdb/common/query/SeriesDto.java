package com.erdouglass.emdb.common.query;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.AbstractSeriesBuilder;
import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.ShowConstants;
import com.erdouglass.emdb.common.ShowStatus;
import com.erdouglass.validation.DateRange;

public record SeriesDto(
    @NotNull @Positive Long id,
    @NotNull @Positive Integer tmdbId,
    @NotBlank @Size(max = ShowConstants.NAME_MAX_LENGTH) String name,
    @DateRange(min = ShowConstants.SERIES_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate firstAirDate,
    @DateRange(min = ShowConstants.SERIES_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate lastAirDate,
    @Min(0) @Max(10) Float score,
    @NotNull @Size(max = ShowConstants.STATUS_MAX_LENGTH) ShowStatus status,
    @Size(max = ShowConstants.SERIES_TYPE_MAX_LENGTH) String type,
    @Size(max = Configuration.URL_MAX_LENGTH) String homepage,
    @Size(min = Configuration.ISO_639_1_LENGTH, max = Configuration.ISO_639_1_LENGTH) String originalLanguage,
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String backdrop,
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String poster,
    @Size(max = ShowConstants.TAGLINE_MAX_LENGTH) String tagline, 
    @Size(max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview) {
  
  public static Builder builder() {
    return new Builder();
  }
  
  @Override
  public String toString() {
    return "SeriesDto[id=" + id
            + ", tmdbId=" + tmdbId
            + ", name=" + name
            + ", firstAirDate=" + firstAirDate
            + ", lastAirDate=" + lastAirDate
            + ", status=" + status
            + "]";
  } 
  
  public static final class Builder extends AbstractSeriesBuilder<Builder> {
    private LocalDate firstAirDate;
    private LocalDate lastAirDate;
    private Long id;

    private Builder() { }

    public SeriesDto build() {
      return new SeriesDto(
            id,
            tmdbId,
            name, 
            firstAirDate,
            lastAirDate,
            score,
            status,
            type,
            homepage,
            originalLanguage,
            backdrop,
            poster,
            tagline,
            overview);
    }

    public Builder id(Long id) {
      this.id = id;
      return this;
    }
    
    public Builder firstAirDate(LocalDate firstAirDate) {
      this.firstAirDate = firstAirDate;
      return this;
    }
    
    public Builder lastAirDate(LocalDate lastAirDate) {
      this.lastAirDate = lastAirDate;
      return this;
    }

    @Override
    protected Builder self() {
      return this;
    }
  }

}
