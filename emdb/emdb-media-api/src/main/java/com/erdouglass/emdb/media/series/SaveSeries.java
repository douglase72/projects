package com.erdouglass.emdb.media.series;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.media.MediaConstants;
import com.erdouglass.emdb.media.image.Image;
import com.erdouglass.emdb.media.show.ShowConstants;
import com.erdouglass.emdb.media.show.ShowStatus;

public record SaveSeries(
    @NotNull @Positive Integer tmdbId,
    @NotBlank @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
    @NotNull @DecimalMin("0") @DecimalMax("10") Float score,
    @NotNull ShowStatus status,
    @NotNull SeriesType type,
    @Valid Image backdrop,
    @Valid Image poster,
    @Size(max = MediaConstants.URL_MAX_LENGTH) String homepage,
    @NotBlank @Size(min = MediaConstants.ISO_639_1_LENGTH, max = MediaConstants.ISO_639_1_LENGTH) String originalLanguage,
    @Size(max = ShowConstants.TAGLINE_MAX_LENGTH) String tagline,
    @Size(max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview) {

  public static Builder builder() {
    return new Builder();
  }
  
  public static Builder builder(SaveSeries command) {
    return builder()
        .tmdbId(command.tmdbId)
        .title(command.title)
        .score(command.score)
        .status(command.status)
        .type(command.type)
        .backdrop(command.backdrop)
        .poster(command.poster)
        .homepage(command.homepage)
        .originalLanguage(command.originalLanguage)
        .tagline(command.tagline)
        .overview(command.overview);
  } 
  
  @Override
  public String toString() {
    return "SaveSeries[tmdbId=" + tmdbId
        + ", title=" + title
        + "]";
  }
  
  public static final class Builder extends SeriesBuilder<Builder> {
    private Integer tmdbId;
        
    private Builder() { }

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
