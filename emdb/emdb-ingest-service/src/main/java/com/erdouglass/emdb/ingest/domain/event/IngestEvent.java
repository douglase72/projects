package com.erdouglass.emdb.ingest.domain.event;

import java.time.Instant;

import com.erdouglass.emdb.shared.kernel.MediaType;
import com.erdouglass.emdb.shared.kernel.PublicId;
import com.erdouglass.emdb.shared.kernel.TmdbId;

public sealed interface IngestEvent permits IngestSubmitted {

  PublicId ingestId();

  MediaType mediaType();

  Instant occurredAt();

  TmdbId tmdbId();
}