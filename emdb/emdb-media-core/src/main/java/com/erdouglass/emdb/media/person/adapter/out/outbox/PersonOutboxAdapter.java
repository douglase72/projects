package com.erdouglass.emdb.media.person.adapter.out.outbox;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.common.TmdbId;
import com.erdouglass.emdb.ingest.IngestMediaCommand;
import com.erdouglass.emdb.ingest.IngestMediaUseCase;
import com.erdouglass.emdb.ingest.IngestType;

@ApplicationScoped
class PersonOutboxAdapter {
  
  @Inject
  IngestMediaUseCase ingestUseCase;

  public void publish() {
    ingestUseCase.ingest(IngestMediaCommand.of(TmdbId.of(1205), IngestType.PERSON));
  }
}
