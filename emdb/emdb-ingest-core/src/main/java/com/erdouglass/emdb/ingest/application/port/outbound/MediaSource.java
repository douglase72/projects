package com.erdouglass.emdb.ingest.application.port.outbound;

import jakarta.validation.constraints.NotNull;

import com.erdouglass.emdb.media.SourceId;

public interface MediaSource {

  MovieDto extract(@NotNull SourceId id);
}
