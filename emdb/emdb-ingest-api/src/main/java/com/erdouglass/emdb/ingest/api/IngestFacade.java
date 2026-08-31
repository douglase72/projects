package com.erdouglass.emdb.ingest.api;

import java.util.UUID;

public interface IngestFacade {

  UUID ingest(IngestCommand command);
}
