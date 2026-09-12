package com.erdouglass.emdb.ingest;

import java.util.UUID;

/// Open-Host Service protects consumers from changes in the Ingest service 
/// domain allowing it to evolve independently of the public API.
public interface IngestMediaUseCase {

  UUID ingest(IngestMediaCommand command);
}
