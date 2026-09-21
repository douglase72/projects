package com.erdouglass.emdb.ingest.domain.exception;

import com.erdouglass.emdb.ingest.domain.model.IngestId;

public class IngestJobNotFoundException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public IngestJobNotFoundException(IngestId id) {
    super("No ingest job with id: %s exists".formatted(id));
  } 
}
