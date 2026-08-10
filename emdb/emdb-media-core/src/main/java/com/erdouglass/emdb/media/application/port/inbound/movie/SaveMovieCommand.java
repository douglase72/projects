package com.erdouglass.emdb.media.application.port.inbound.movie;

import java.util.Objects;

import com.erdouglass.emdb.media.TmdbId;
import com.erdouglass.emdb.media.domain.movie.MovieDetails;
import com.erdouglass.emdb.media.domain.movie.ReleaseDate;
import com.erdouglass.emdb.media.domain.movie.Title;
import com.erdouglass.emdb.media.domain.shared.LanguageCode;
import com.erdouglass.emdb.media.domain.shared.Score;

public record SaveMovieCommand(
    TmdbId tmdbId,
    MovieDetails details) {

  public static Builder builder() { return new Builder(); }
  
  public static final class Builder {
    private TmdbId tmdbId;
    private Title title;
    private ReleaseDate releaseDate;
    private Score score;
    private LanguageCode originalLanguage;
    
    private Builder() {}

    public SaveMovieCommand build() {
      Objects.requireNonNull(tmdbId, "tmdbId must not be null");
      Objects.requireNonNull(title, "title must not be null");
      return new SaveMovieCommand(tmdbId, MovieDetails.builder()
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
    
    public Builder tmdbId(TmdbId tmdbId) {
      this.tmdbId = tmdbId;
      return this;
    }
  }
}