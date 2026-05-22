package com.erdouglass.emdb.common.series;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.SaveCommand;
import com.erdouglass.emdb.common.ShowConstants;
import com.erdouglass.emdb.common.ShowStatus;

public record SaveSeries(
    @NotNull @Positive Integer tmdbId,
    @NotBlank @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
    @NotNull @Min(0) @Max(10) Float score,
    @NotNull ShowStatus status,
    @NotNull SeriesType type,
    String tmdbBackdrop,
    String tmdbPoster,
    @Size(min = 1, max = Configuration.URL_MAX_LENGTH) String homepage,
    @NotBlank @Size(min = Configuration.ISO_639_1_LENGTH, max = Configuration.ISO_639_1_LENGTH) String originalLanguage,
    @Size(max = ShowConstants.TAGLINE_MAX_LENGTH) String tagline, 
    @Size(min = 1, max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview) implements SaveCommand {

  public static Builder builder() {
    return new Builder();
  }
  
  @Override
  public String toString() {
    return "SaveSeries[tmdbId=" + tmdbId
        + ", title=" + title
        + "]";
  }
  
  public static Builder builder(SaveSeries command) {
    return builder()
        .tmdbId(command.tmdbId)
        .title(command.title)
        .score(command.score)
        .status(command.status)
        .type(command.type)
        .backdrop(command.tmdbBackdrop)
        .poster(command.tmdbPoster)
        .homepage(command.homepage)
        .originalLanguage(command.originalLanguage)
        .tagline(command.tagline)
        .overview(command.overview);
  }
  
  public static final class Builder extends SeriesBuilder<Builder> {
    private Integer tmdbId;
        
    private Builder() {}

    public SaveSeries build() {
      return new SaveSeries(
            tmdbId,
            title, 
            score,
            status,
            type,
            backdrop,
            poster,            
            homepage,
            originalLanguage,
            tagline,
            overview);
    }
    
    public Builder tmdbId(Integer tmdbId) {
      this.tmdbId = tmdbId;
      return this;
    }

    @Override
    protected Builder self() {
      return this;
    }
  }  
}
