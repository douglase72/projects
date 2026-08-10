package com.erdouglass.emdb.media.application.port.inbound.movie;

import java.util.Objects;

import com.erdouglass.emdb.media.domain.movie.MovieDetails;
import com.erdouglass.emdb.media.domain.movie.MoviePublicId;
import com.erdouglass.emdb.media.domain.movie.ReleaseDate;
import com.erdouglass.emdb.media.domain.movie.Title;
import com.erdouglass.emdb.media.domain.shared.LanguageCode;
import com.erdouglass.emdb.media.domain.shared.Score;
import com.erdouglass.emdb.media.domain.shared.Version;

public record UpdateMovieCommand(
    MoviePublicId publicId,
    Version version, 
    MovieDetails details) {

  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private MoviePublicId publicId;
    private Version version;
    private Title title;
    private ReleaseDate releaseDate;
    private Score score;
    private LanguageCode originalLanguage;
    
    private Builder() {}

    public UpdateMovieCommand build() {
      Objects.requireNonNull(publicId, "publicId must not be null");
      Objects.requireNonNull(version, "version must not be null");
      return new UpdateMovieCommand(publicId, version,
          MovieDetails.builder()
            .title(title)
            .releaseDate(releaseDate)
            .score(score)
            .originalLanguage(originalLanguage)
            .build());
    }
    
    public Builder originalLanguage(LanguageCode originalLanguage) {
      this.originalLanguage = originalLanguage;
      return this;
    }
    
    public Builder publicId(MoviePublicId publicId) {
      this.publicId = publicId;
      return this;
    }   
    
    public Builder releaseDate(ReleaseDate releaseDate) {
      this.releaseDate = releaseDate;
      return this;
    }
    
    public Builder score(Score score) {
      this.score = score;
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
