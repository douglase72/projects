package com.erdouglass.emdb.common.command;

import com.erdouglass.emdb.common.AbstractSeriesBuilder;
import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.ShowConstants;
import com.erdouglass.emdb.common.ShowStatus;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record SeriesCreateCommand(
    @NotNull @Positive Integer tmdbId,
    @NotBlank @Size(max = ShowConstants.NAME_MAX_LENGTH) String name,
    @Min(0) @Max(10) Float score,
    @NotNull ShowStatus status,
    @Size(max = Configuration.URL_MAX_LENGTH) String homepage,
    @Size(min = Configuration.ISO_639_1_LENGTH, max = Configuration.ISO_639_1_LENGTH) String originalLanguage,
    @Size(max = ShowConstants.SERIES_TYPE_MAX_LENGTH) String type,
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String backdrop,
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String poster,
    @Size(max = ShowConstants.TAGLINE_MAX_LENGTH) String tagline, 
    @Size(max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview) {

  public static Builder builder() {
    return new Builder();
  }
  
  @Override
  public String toString() {
    return "SeriesCreateCommand[tmdbId=" + tmdbId
            + ", name=" + name
            + "]";
  }
  
  public static final class Builder extends AbstractSeriesBuilder<Builder> {
    
    private Builder() {}
    
    public SeriesCreateCommand build() {
      return new SeriesCreateCommand(
            tmdbId,
            name, 
            score,
            status,
            homepage,
            originalLanguage,
            type,
            backdrop,
            poster,
            tagline,
            overview);
    }
    
    @Override
    protected Builder self() {
      return this;
    }
  }
  
}
