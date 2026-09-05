package com.erdouglass.emdb.ingest.domain.model;

import java.util.Objects;

import com.erdouglass.emdb.media.api.TmdbId;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public final class Ingest {
  private final IngestId id;
  private final TmdbId tmdbId;
  private final IngestType type;
  
  private Ingest(IngestId id, TmdbId tmdbId, IngestType type) {
    this.id = Objects.requireNonNull(id, "id must not be null");
    this.tmdbId = Objects.requireNonNull(tmdbId, "tmdbId must not be null");
    this.type = Objects.requireNonNull(type, "type must not be null");
  }
  
  public static Ingest submit(TmdbId tmdbId, IngestType type) {
    var ingest = new Ingest(IngestId.newId(), tmdbId, type);
    return ingest;
  }
  
  @Override
  public String toString() {
    return getClass().getSimpleName() + "[id=" + id.value()
      + ", tmdbId=" + tmdbId.value()
      + ", type=" + type
      + "]";
  }
}
