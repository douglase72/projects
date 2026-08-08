package com.erdouglass.emdb.ingest.application.port.outbound;

import jakarta.validation.constraints.NotNull;

import com.erdouglass.emdb.ingest.domain.model.IngestType;
import com.erdouglass.emdb.media.TmdbId;

public interface MediaSource {

  Media extract(@NotNull TmdbId tmdbId, @NotNull IngestType type);
}
