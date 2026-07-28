package com.erdouglass.emdb.ingest.application.port.inbound;

import java.util.Objects;

import com.erdouglass.emdb.media.MediaType;
import com.erdouglass.emdb.media.SourceId;

public record IngestMediaCommand(SourceId sourceId, MediaType mediaType) {

  public IngestMediaCommand {
    Objects.requireNonNull(sourceId, "source id must not be null");
    Objects.requireNonNull(mediaType, "media type must not be null");
  }
  
  public static IngestMediaCommand of(SourceId sourceId, MediaType mediaType) {
    return new IngestMediaCommand(sourceId, mediaType);
  }
}
