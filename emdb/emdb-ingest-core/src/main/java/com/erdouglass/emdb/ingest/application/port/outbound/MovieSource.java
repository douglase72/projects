package com.erdouglass.emdb.ingest.application.port.outbound;

import jakarta.validation.constraints.NotNull;

import com.erdouglass.emdb.media.TmdbId;

public interface MovieSource {

  Movie extract(@NotNull TmdbId tmdbId);
}
