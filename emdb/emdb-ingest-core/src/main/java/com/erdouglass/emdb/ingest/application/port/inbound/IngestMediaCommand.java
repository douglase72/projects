package com.erdouglass.emdb.ingest.application.port.inbound;

import java.util.Objects;

import com.erdouglass.emdb.ingest.domain.model.IngestType;
import com.erdouglass.emdb.media.SourceId;

public record IngestMediaCommand(SourceId sourceId, IngestType type) {

  public IngestMediaCommand {
    Objects.requireNonNull(sourceId, "source id must not be null");
    Objects.requireNonNull(type, "type must not be null");
  }
  
  public static IngestMediaCommand of(SourceId sourceId, IngestType type) {
    return new IngestMediaCommand(sourceId, type);
  }
}
