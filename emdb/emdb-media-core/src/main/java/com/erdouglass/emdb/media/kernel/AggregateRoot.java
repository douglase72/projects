package com.erdouglass.emdb.media.kernel;

import java.util.Objects;
import java.util.Optional;

import com.erdouglass.emdb.media.api.TmdbId;

public abstract class AggregateRoot {
  private final AggregateId id;
  private final PublicId publicId;
  private final TmdbId tmdbId;
  private final Version version;
  
  protected AggregateRoot(AggregateId id, PublicId publicId, TmdbId tmdbId, Version version) {
    this.id = Objects.requireNonNull(id, "aggregate id is required");
    this.publicId = publicId;
    this.tmdbId = Objects.requireNonNull(tmdbId, "aggregate TMDB id is required");
    this.version = Objects.requireNonNull(version, "aggregate version id is required");
  }
  
  public AggregateId id() { return id; }
  public Optional<PublicId> publicId() { return Optional.ofNullable(publicId); }
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
