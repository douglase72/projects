package com.erdouglass.emdb.media.movie.domain.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.erdouglass.emdb.media.kernel.AggregateRoot;
import com.erdouglass.emdb.media.kernel.LanguageCode;
import com.erdouglass.emdb.media.kernel.Overview;
import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.kernel.Score;
import com.erdouglass.emdb.media.kernel.Title;
import com.erdouglass.emdb.media.kernel.TmdbCreditId;
import com.erdouglass.emdb.media.kernel.TmdbId;
import com.erdouglass.emdb.media.kernel.Version;
import com.erdouglass.emdb.media.movie.domain.event.DomainEvent;
import com.erdouglass.emdb.media.movie.domain.event.MovieCreated;
import com.erdouglass.emdb.media.movie.domain.event.MovieUpdated;

public final class Movie extends AggregateRoot {
  private MovieDetails details;
  private final List<MovieCredit> credits = new ArrayList<>();
  private final List<DomainEvent> domainEvents = new ArrayList<>();
  
  private Movie(PublicId id, TmdbId tmdbId, Version version, MovieDetails details) {
    super(id, tmdbId, version);
    this.details = Objects.requireNonNull(details, "details are required");
  }
  
  public static Movie create(TmdbId tmdbId, MovieDto dto) {
    var movie = new Movie(PublicId.newId(), tmdbId, Version.of(0L), dto.details());
    movie.syncCredits(dto.credits());
    movie.raise(MovieCreated.of(movie.id(), movie.tmdbId(), movie.title()));
    return movie;
  }
  
  public void update(MovieDetails details) {
    this.details = details;
    raise(MovieUpdated.of(id(), tmdbId(), title()));
  }
  
  public void update(MovieDto dto) {
    this.details = dto.details();
    syncCredits(dto.credits());
    raise(MovieUpdated.of(id(), tmdbId(), title()));
  }
  
  public static Movie rehydrate(PublicId id, TmdbId tmdbId, Version version, MovieDetails details) {
    return new Movie(id, tmdbId, version, details);
  }
  
  public List<DomainEvent> pullEvents() {
    var events = List.copyOf(domainEvents);
    domainEvents.clear();
    return events;
  }
  
  public Title title() { return details.title(); }
  public Optional<ReleaseDate> releaseDate() { return Optional.ofNullable(details.releaseDate()); }
  public Optional<Score> score() { return Optional.ofNullable(details.score()); }
  public Optional<LanguageCode> originalLanguage() { return Optional.ofNullable(details.originalLanguage()); }
  public Optional<Overview> overview() { return Optional.ofNullable(details.overview()); }
  public List<MovieCredit> credits() { return credits; }
  
  @Override
  public String toString() {
    return getClass().getSimpleName() + "[id=" + id().value() 
        + ", tmdbId=" + tmdbId().value()
        + ", version=" + version().value()
        + ", title=" + title().value()
        + ", releaseDate=" + releaseDate().map(ReleaseDate::toLocalDate).orElse(null)
        + "]";
  }
  
  private void raise(DomainEvent event) {
    domainEvents.add(event);
  }
  
  private void syncCredits(List<MovieCredit> incoming) {
    var existing = credits.stream()
        .collect(Collectors.toMap(MovieCredit::tmdbId, Function.identity()));
    var seen = new HashSet<TmdbCreditId>();
    
    var synced = new ArrayList<MovieCredit>();
    for (var credit : incoming) {
      if (!seen.add(credit.tmdbId())) {
        throw new IllegalArgumentException("duplicate credit: " + credit);
      }
      var match = existing.get(credit.tmdbId());
      if (match == null) {
        switch (credit) {
          case CastCredit c -> synced.add(CastCredit.create(c.details()));
          case CrewCredit c -> synced.add(CrewCredit.create(c.details()));
        }
      } else {
        match.update(credit.details());
        synced.add(match);
      }
    }
    
    credits.clear();
    credits.addAll(synced);
  }
}
