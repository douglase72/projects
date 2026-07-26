package com.erdouglass.emdb.ingest.domain.model;

import java.util.Objects;

import com.erdouglass.emdb.media.MediaType;

public class Ingest {

  private final IngestId id;
  private final MediaType mediaType;
  private final TmdbId tmdbId;
  
  private Ingest(IngestId id, TmdbId tmdbId, MediaType mediaType) {
    this.id = Objects.requireNonNull(id, "id");
    this.tmdbId = Objects.requireNonNull(tmdbId, "tmdbId"); 
    this.mediaType = Objects.requireNonNull(mediaType, "mediaType");
  }
  
  public static Ingest submit(IngestId id, TmdbId tmdbId, MediaType mediaType) {
    var ingest = new Ingest(id, tmdbId, mediaType);
    return ingest;
  }
  
  public IngestId id() { return id; }
  public MediaType mediaType() { return mediaType; }
  public TmdbId tmdbId() { return tmdbId; }
}
