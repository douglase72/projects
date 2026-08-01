package com.erdouglass.emdb.ingest.domain.model;

import java.util.Objects;

import com.erdouglass.emdb.media.SourceId;
import com.erdouglass.emdb.media.SourceId.Source;

public record IngestSource(
    SourceId source,
    IngestType type) {

  public IngestSource {
    Objects.requireNonNull(source, "source must not be null");
    Objects.requireNonNull(type, "type must not be null");
  }
  
  public static IngestSource of(SourceId source, IngestType type) {
    return new IngestSource(source, type);
  }
  
  public String id() {
    return source.id();
  }
  
  public Source provider() {
    return source.provider();
  }
}
