package com.erdouglass.emdb.ingest.application.port.outbound;

import jakarta.validation.constraints.NotNull;

public interface MovieEmitter {

  void emit(@NotNull Movie movie);
}
