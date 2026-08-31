package com.erdouglass.emdb.ingest.adapter.in.media;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.ingest.api.IngestCommand;
import com.erdouglass.emdb.ingest.api.IngestFacade;
import com.erdouglass.emdb.ingest.application.port.in.IngestMediaCommand;
import com.erdouglass.emdb.ingest.application.port.in.SubmitIngestUseCase;
import com.erdouglass.emdb.ingest.domain.model.TmdbId;

@ApplicationScoped
class MediaAdapter implements IngestFacade {
  
  @Inject
  SubmitIngestUseCase submitUseCase;

  @Override
  public UUID ingest(IngestCommand command) {
    var cmd = IngestMediaCommand.of(TmdbId.of(command.tmdbId()), command.ingestType());
    var id = submitUseCase.submit(cmd);
    return id.value();
  }
}
