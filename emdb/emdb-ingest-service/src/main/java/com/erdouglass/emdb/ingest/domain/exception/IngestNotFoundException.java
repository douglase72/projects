package com.erdouglass.emdb.ingest.domain.exception;

import com.erdouglass.emdb.shared.kernel.PublicId;

public class IngestNotFoundException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public IngestNotFoundException(PublicId id) {
    super("No ingest job with id: %s exists".formatted(id));
  }
}
