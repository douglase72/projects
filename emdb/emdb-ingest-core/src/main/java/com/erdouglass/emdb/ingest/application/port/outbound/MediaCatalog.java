package com.erdouglass.emdb.ingest.application.port.outbound;

import jakarta.validation.constraints.NotNull;

public interface MediaCatalog {

  void load(@NotNull Media media);
}
