package com.erdouglass.emdb.ingest.domain.exception;

import com.erdouglass.emdb.ingest.domain.model.IngestId;
import com.erdouglass.emdb.ingest.domain.model.IngestStage;

public final class IllegalTransitionException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public IllegalTransitionException(IngestId id, IngestStage from, IngestStage to) {
    super("Ingest job: %s connot transition from %s to %s".formatted(id, from, to));
  } 
}
