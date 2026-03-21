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
import com.erdouglass.emdb.common.SeriesType;
import com.erdouglass.emdb.common.ShowConstants;
import com.erdouglass.emdb.common.ShowStatus;
import com.erdouglass.emdb.common.ValidImage;
import com.erdouglass.validation.DateRange;

public record SeriesDto(
    @NotNull @Positive Long id,
    @NotNull @Positive Integer tmdbId,
    @NotBlank @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
    @DateRange(min = ShowConstants.SERIES_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate firstAirDate,
    @DateRange(min = ShowConstants.SERIES_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate lastAirDate,    
    @Min(0) @Max(10) Float score,
    ShowStatus status,
    SeriesType type,
    @ValidImage String backdrop,
    @ValidImage String poster,
    @Size(min = 1, max = Configuration.URL_MAX_LENGTH) String homepage,
    @Size(min = Configuration.ISO_639_1_LENGTH, max = Configuration.ISO_639_1_LENGTH) String originalLanguage,
    @Size(max = ShowConstants.TAGLINE_MAX_LENGTH) String tagline,
    @Size(min = 1, max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview) {
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder extends AbstractSeriesBuilder<Builder> {
    private String backdrop;
    private Long id;
    private String poster;
    private Integer tmdbId;

    private Builder() { }

    public SeriesDto build() {
      return new SeriesDto(
            id,
            tmdbId,
            title, 
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
    
    public Builder backdrop(String backdrop) {
      this.backdrop = backdrop;
      return this;
    }

    public Builder id(Long id) {
      this.id = id;
      return this;
    }
    
    public Builder poster(String poster) {
      this.poster = poster;
      return this;
    }
    
    public Builder tmdbId(Integer tmdbId) {
      this.tmdbId = tmdbId;
      return self();
    }

    @Override
    protected Builder self() {
      return this;
    }
  }  

}
