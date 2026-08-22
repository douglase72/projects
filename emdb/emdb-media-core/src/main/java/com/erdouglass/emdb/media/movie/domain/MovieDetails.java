package com.erdouglass.emdb.media.movie.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.erdouglass.emdb.media.kernel.LanguageCode;
import com.erdouglass.emdb.media.kernel.Overview;
import com.erdouglass.emdb.media.kernel.Score;
import com.erdouglass.emdb.media.kernel.Title;

/// The mutable content of a title — everything about a movie that can change
/// without it becoming a different movie.
///
/// Kept separate from [Movie] so that identity and lifecycle live in the
/// aggregate while content stays an immutable value. Replacing details is
/// therefore a single assignment, and comparing two revisions is a plain value
/// comparison, which is what [MovieField#diff] relies on.
///
/// Only the title is required. The remaining components are optional both in the
/// sense that they may be absent and in the sense that absence is meaningful:
/// an empty component is a field the catalogue does not know, and clearing a
/// field is a recordable change.
///
/// @param title the display title, never `null`
/// @param releaseDate the theatrical or first-publication date, if known
/// @param score the aggregate rating from 0 to 10, if rated
/// @param originalLanguage the language the title was produced in, if known
/// @param overview the synopsis, if available
public record MovieDetails(
    Title title,
    Optional<ReleaseDate> releaseDate,
    Optional<Score> score,
    Optional<LanguageCode> originalLanguage,
    Optional<Overview> overview,
    List<MovieCredit> credits) {
  
  public static Builder builder() { return new Builder(); }
  
  public static final class Builder {
    private Title title;
    private ReleaseDate releaseDate;
    private Score score;
    private LanguageCode originalLanguage;
    private Overview overview;
    private List<MovieCredit> credits = new ArrayList<>();
    
    private Builder() {}

    public MovieDetails build() {
      Objects.requireNonNull(title, "title must not be null");
      return new MovieDetails(
          title, 
          Optional.ofNullable(releaseDate), 
          Optional.ofNullable(score),
          Optional.ofNullable(originalLanguage),
          Optional.ofNullable(overview),
          credits);
    }
    
    public Builder credits(List<MovieCredit> credits) {
      this.credits = new ArrayList<>(credits);
      return this;
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
