package com.erdouglass.emdb.ingest.application.port.out;

import com.erdouglass.emdb.ingest.messaging.IngestCommand;

public interface IngestCommandRepository {

  void save(IngestCommand command);
}
