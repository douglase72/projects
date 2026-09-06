package com.erdouglass.emdb.media.kernel;

import java.util.Objects;
import java.util.Optional;

import com.erdouglass.emdb.media.api.TmdbId;

public abstract class AggregateRoot {
  private final PublicId id;
  private final SurrogateId surrogateId;
  private final TmdbId tmdbId;
  private final Version version;
  
  protected AggregateRoot(PublicId id, SurrogateId surrogateId, TmdbId tmdbId, Version version) {
    this.id = Objects.requireNonNull(id, "id is required");
    this.surrogateId = surrogateId;
    this.tmdbId = Objects.requireNonNull(tmdbId, "TMDB id is required");
    this.version = Objects.requireNonNull(version, "version is required");
  }
  
  public PublicId id() { return id; }
  public Optional<SurrogateId> surrogateId() { return Optional.ofNullable(surrogateId); }
  public TmdbId tmdbId() { return tmdbId; }
  public Version version() { return version; }
  
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
    AggregateRoot other = (AggregateRoot) obj;
    return Objects.equals(id, other.id);
  }
}
