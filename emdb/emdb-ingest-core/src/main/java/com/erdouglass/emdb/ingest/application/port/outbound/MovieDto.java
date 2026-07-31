package com.erdouglass.emdb.ingest.application.port.outbound;

import java.util.Objects;

import com.erdouglass.emdb.media.OriginalLanguage;
import com.erdouglass.emdb.media.ReleaseDate;
import com.erdouglass.emdb.media.SourceId;
import com.erdouglass.emdb.media.Title;

public record MovieDto(
    SourceId sourceId,
    Title title,
    ReleaseDate releaseDate,
    OriginalLanguage originalLanguage) {
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private SourceId sourceId;
    private Title title;
    private ReleaseDate releaseDate;
    private OriginalLanguage originalLanguage;
    
    private Builder() {}

    public MovieDto build() {
      Objects.requireNonNull(sourceId, "sourceId must not be null");
      Objects.requireNonNull(title, "title must not be null");
      Objects.requireNonNull(originalLanguage, "originalLanguage must not be null");
      return new MovieDto(
          sourceId,
          title, 
          releaseDate, 
          originalLanguage);
    }
    
    public Builder originalLanguage(OriginalLanguage originalLanguage) {
      this.originalLanguage = originalLanguage;
      return this;
    }
    
    public Builder sourceId(SourceId sourceId) {
      this.sourceId = sourceId;
      return this;
    }
    
    public Builder releaseDate(ReleaseDate releaseDate) {
      this.releaseDate = releaseDate;
      return this;
    }
    
    public Builder title(Title title) {
      this.title = title;
      return this;
    }
  }  
}
