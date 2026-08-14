package com.erdouglass.emdb.media.domain.movie;

import java.util.Objects;
import java.util.Optional;

import com.erdouglass.emdb.media.domain.shared.LanguageCode;
import com.erdouglass.emdb.media.domain.shared.Overview;
import com.erdouglass.emdb.media.domain.shared.Score;

public record MovieDetails(
    Title title,
    Optional<ReleaseDate> releaseDate,
    Optional<Score> score,
    Optional<LanguageCode> originalLanguage,
    Optional<Overview> overview) {
  
  public static Builder builder() { return new Builder(); }
  
  public static final class Builder {
    private Title title;
    private ReleaseDate releaseDate;
    private Score score;
    private LanguageCode originalLanguage;
    private Overview overview;
    
    private Builder() {}

    public MovieDetails build() {
      Objects.requireNonNull(title, "title must not be null");
      return new MovieDetails(
          title, 
          Optional.ofNullable(releaseDate), 
          Optional.ofNullable(score),
          Optional.ofNullable(originalLanguage),
          Optional.ofNullable(overview));
    }
    
    public Builder originalLanguage(LanguageCode originalLanguage) {
      this.originalLanguage = originalLanguage;
      return this;
    }
    
    public Builder overview(Overview overview) {
      this.overview = overview;
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
  }
}
