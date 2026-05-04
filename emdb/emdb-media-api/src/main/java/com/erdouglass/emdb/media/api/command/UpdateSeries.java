package com.erdouglass.emdb.media.api.command;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.api.Configuration;
import com.erdouglass.emdb.common.api.ShowConstants;
import com.erdouglass.emdb.media.api.SeriesType;
import com.erdouglass.emdb.media.api.ShowStatus;

public record UpdateSeries(    
    @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
    @Min(0) @Max(10) Float score,
    ShowStatus status,
    SeriesType type,
    UUID backdrop,
    UUID poster,
    @Size(min = 1, max = Configuration.URL_MAX_LENGTH) String homepage,
    @Size(min = Configuration.ISO_639_1_LENGTH, max = Configuration.ISO_639_1_LENGTH) String originalLanguage,
    @Size(max = ShowConstants.TAGLINE_MAX_LENGTH) String tagline, 
    @Size(min = 1, max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview) {
  
  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder extends AbstractSeriesBuilder<Builder> {
    private UUID backdrop;
    private UUID poster;
        
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
    
    public Builder backdrop(UUID backdrop) {
      this.backdrop = backdrop;
      return this;
    }
    
    public Builder poster(UUID poster) {
      this.poster = poster;
      return this;
    }

    @Override
    protected Builder self() {
      return this;
    }
  }  
}
