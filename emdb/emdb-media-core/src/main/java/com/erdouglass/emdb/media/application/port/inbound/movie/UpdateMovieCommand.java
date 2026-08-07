package com.erdouglass.emdb.media.application.port.inbound;

import java.util.Objects;

import com.erdouglass.emdb.media.OriginalLanguage;
import com.erdouglass.emdb.media.ReleaseDate;
import com.erdouglass.emdb.media.Title;
import com.erdouglass.emdb.media.domain.shared.Version;

public record UpdateMovieCommand(
    Version version, 
    Title title,
    ReleaseDate releaseDate,
    OriginalLanguage originalLanguage) {
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private Version version;
    private Title title;
    private ReleaseDate releaseDate;
    private OriginalLanguage originalLanguage;
    
    private Builder() {}

    public UpdateMovieCommand build() {
      Objects.requireNonNull(version, "version must not be null");
      Objects.requireNonNull(title, "title must not be null");
      Objects.requireNonNull(originalLanguage, "originalLanguage must not be null");
      return new UpdateMovieCommand(
          version,
          title, 
          releaseDate, 
          originalLanguage);
    }
    
    public Builder originalLanguage(final OriginalLanguage originalLanguage) {
      this.originalLanguage = originalLanguage;
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
    
    public Builder version(Version version) {
      this.version = version;
      return this;
    }
  }
}
