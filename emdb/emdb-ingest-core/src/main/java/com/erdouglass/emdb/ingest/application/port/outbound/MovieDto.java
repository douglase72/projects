package com.erdouglass.emdb.ingest.application.port.outbound;

import java.util.Objects;
import java.util.Optional;

import com.erdouglass.emdb.ingest.domain.model.IngestType;
import com.erdouglass.emdb.media.LanguageCode;
import com.erdouglass.emdb.media.ReleaseDate;
import com.erdouglass.emdb.media.Score;
import com.erdouglass.emdb.media.Title;
import com.erdouglass.emdb.media.TmdbId;

public record MovieDto(
    TmdbId tmdbId,
    Title title,
    Optional<ReleaseDate> releaseDate,
    Optional<Score> score,
    Optional<LanguageCode> originalLanguage) implements Media {
  
  @Override
  public IngestType type() {
    return IngestType.MOVIE;
  }
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private TmdbId tmdbId;
    private Title title;
    private ReleaseDate releaseDate;
    private Score score;
    private LanguageCode originalLanguage;
    
    private Builder() {}

    public MovieDto build() {
      Objects.requireNonNull(tmdbId, "tmdbId must not be null");
      Objects.requireNonNull(title, "title must not be null");
      return new MovieDto(
          tmdbId,
          title, 
          Optional.ofNullable(releaseDate),
          Optional.ofNullable(score),
          Optional.ofNullable(originalLanguage));
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
