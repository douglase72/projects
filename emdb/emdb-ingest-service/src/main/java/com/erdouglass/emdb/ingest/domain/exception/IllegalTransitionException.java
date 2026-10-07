package com.erdouglass.emdb.ingest.domain.exception;

import com.erdouglass.emdb.ingest.domain.model.IngestStatus;
import com.erdouglass.emdb.shared.kernel.PublicId;

public final class IllegalTransitionException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public IllegalTransitionException(PublicId id, IngestStatus from, IngestStatus to) {
    super("Ingest job: %s connot transition from %s to %s".formatted(id, from, to));
  } 
}
