package com.erdouglass.emdb.media.series;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.media.MediaConstants;
import com.erdouglass.emdb.media.image.Image;
import com.erdouglass.emdb.media.show.ShowConstants;
import com.erdouglass.emdb.media.show.ShowStatus;

public record UpdateSeries(
    @NotBlank @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
    @NotNull @DecimalMin("0") @DecimalMax("10") Float score,
    @NotNull ShowStatus status,
    @NotNull SeriesType type,
    @Valid Image backdrop,
    @Valid Image poster,
    @Size(min = 1, max = MediaConstants.URL_MAX_LENGTH) String homepage,
    @NotBlank @Size(min = MediaConstants.ISO_639_1_LENGTH, max = MediaConstants.ISO_639_1_LENGTH) String originalLanguage,
    @Size(max = ShowConstants.TAGLINE_MAX_LENGTH) String tagline,
    @Size(min = 1, max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview) {
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static Builder builder(UpdateSeries command) {
    return builder()
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

  public static final class Builder extends SeriesBuilder<Builder> {
        
    private Builder() { }

    public UpdateSeries build() {
      return new UpdateSeries(
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

    @Override
    protected Builder self() {
      return this;
    }
  }    
}
