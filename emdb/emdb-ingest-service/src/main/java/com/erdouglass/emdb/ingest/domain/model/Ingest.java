package com.erdouglass.emdb.ingest.domain.model;

import java.util.Objects;

import com.erdouglass.emdb.shared.kernel.MediaType;
import com.erdouglass.emdb.shared.kernel.TmdbId;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public final class Ingest {
  private final IngestId id;
  private final TmdbId tmdbId;
  private final MediaType mediaType;
  
  private Ingest(IngestId id, TmdbId tmdbId, MediaType mediaType) {
    this.id = Objects.requireNonNull(id, "id must not be null");
    this.tmdbId = Objects.requireNonNull(tmdbId, "tmdbId must not be null");
    this.mediaType = Objects.requireNonNull(mediaType, "mediaType must not be null");
  }
  
  public static Ingest submit(TmdbId tmdbId, MediaType mediaType) {
    var job = new Ingest(IngestId.newId(), tmdbId, mediaType);
    return job;
  }
  
  @Override
  public String toString() {
    return getClass().getSimpleName() + "[id" + id.value()
      + ", tmdbId=" + tmdbId.value()
      + ", mediaType=" + mediaType
      + "]";
  }
}
