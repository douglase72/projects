package com.erdouglass.emdb.media.movie.domain.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.erdouglass.emdb.media.api.TmdbId;
import com.erdouglass.emdb.media.kernel.Credit;
import com.erdouglass.emdb.media.kernel.Credit.CastDto;
import com.erdouglass.emdb.media.kernel.Credit.CrewDto;
import com.erdouglass.emdb.media.kernel.LanguageCode;
import com.erdouglass.emdb.media.kernel.Overview;
import com.erdouglass.emdb.media.kernel.Score;
import com.erdouglass.emdb.media.kernel.Title;
import com.erdouglass.emdb.media.kernel.TmdbCreditId;
import com.erdouglass.emdb.media.kernel.Version;

/// Movie aggregate.
///
/// A movie carries two identifiers, each with a different lifetime and a
/// different audience:
///
/// * [MovieId] — surrogate id assigned by the application when the aggregate is
///   created. This is the Java identity used by [#equals(Object)] and
///   [#hashCode()], and it is never exposed to the public.
/// * [TmdbId] — natural id supplied by the upstream catalogue, present from
///   creation and never reassigned.
public final class Movie {
  private final MovieId id;
  private final TmdbId tmdbId;
  private final Version version;
  
  private MovieDetails details;
  private final List<MovieCredit> credits =  new ArrayList<>();
  
  private Movie(
      MovieId id, 
      TmdbId tmdbId, 
      Version version, 
      MovieDetails details) {
    this.id = Objects.requireNonNull(id, "id is required");
    this.tmdbId = Objects.requireNonNull(tmdbId, "TMDB id is required");
    this.version = Objects.requireNonNull(version, "version is required");
    this.details = Objects.requireNonNull(details, "details are required");
  }
  
  public static Movie create(TmdbId tmdbId, MovieDetails details, List<Credit> credits) {
    Movie movie = new Movie(MovieId.newId(), tmdbId, Version.of(0L), details);
    movie.syncCredits(credits);
    return movie;
  }
  
  public static Movie rehydrate(
      MovieId id, 
      TmdbId tmdbId, 
      Version version, 
      MovieDetails details) {
    return new Movie(id, tmdbId, version, details);
  }
  
  public void update(MovieDetails details, List<Credit> credits) {
    this.details = Objects.requireNonNull(details, "details are required");
    syncCredits(credits);
  }  
  
  public MovieId id() { return id; } 
  public TmdbId tmdbId() { return tmdbId; }
  public Version version() { return version; }
  public Title title() { return details.title(); }
  public Optional<ReleaseDate> releaseDate() { return Optional.ofNullable(details.releaseDate()); }
  public Optional<Score> score() { return Optional.ofNullable(details.score()); }
  public Optional<LanguageCode> originalLanguage() { return Optional.ofNullable(details.originalLanguage()); }
  public Optional<Overview> overview() { return Optional.ofNullable(details.overview()); }
  public MovieDetails details() { return details; }
  public List<MovieCredit> credits() { return List.copyOf(credits); }
  
  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Movie other = (Movie) obj;
    return Objects.equals(id, other.id);
  }
  
  @Override
  public String toString() {
    return getClass().getSimpleName() + "[id=" + id.value()
        + ", tmdbId=" + tmdbId.value()
        + ", version=" + version.value()
        + ", title=" + title().value()
        + ", releaseDate=" + releaseDate().map(ReleaseDate::toLocalDate).orElse(null)
        + ", score=" + score().map(Score::value).orElse(null)        
        + "]";
  }
  
  private void syncCredits(List<Credit> incoming) {
    var existing = credits.stream()
        .collect(Collectors.toMap(MovieCredit::tmdbId, Function.identity()));
    var seen = new HashSet<TmdbCreditId>();
    
    for (var credit : incoming) {
      if (!seen.add(credit.tmdbId())) {
        throw new IllegalArgumentException("duplicate credit: " + credit);
      }
      var match = existing.get(credit.tmdbId());
      if (match == null) {
        switch (credit) {
          case CastDto c -> credits.add(CastCredit.create(c));
          case CrewDto c -> credits.add(CrewCredit.create(c));
        }
      } else {
        match.update(credit);
      }
    }
  }
}
