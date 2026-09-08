package com.erdouglass.emdb.media.kernel;

import java.util.Objects;

import com.erdouglass.common.rest.StaleVersionException;
import com.erdouglass.emdb.media.api.TmdbId;

public abstract class AggregateRoot {
  private final PublicId id;
  private final TmdbId tmdbId;
  private final Version version;
  
  protected AggregateRoot(PublicId id, TmdbId tmdbId, Version version) {
    this.id = Objects.requireNonNull(id, "id is required");
    this.tmdbId = Objects.requireNonNull(tmdbId, "TMDB id is required");
    this.version = Objects.requireNonNull(version, "version is required");
  }
  
  public void checkVersion(Version expected) {
    if (version == null || !version.equals(expected)) {
      throw new StaleVersionException(version.value().toString());
    }
  }
  
  public PublicId id() { return id; }
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
