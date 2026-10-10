package com.erdouglass.emdb.ingest.application.port.out;

import com.erdouglass.common.messaging.MessageId;

public interface IngestEventRepository {
  
  boolean existsById(MessageId id);
}
