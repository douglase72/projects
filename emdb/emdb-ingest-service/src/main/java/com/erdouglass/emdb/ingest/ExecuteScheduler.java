package com.erdouglass.emdb.ingest;

import jakarta.validation.constraints.NotNull;

/// Command requesting an immediate, out-of-band run of the scheduler for a
/// specific [MediaType]. Sent to [IngestResource] over HTTP; not published
/// to a queue.
public record ExecuteScheduler(@NotNull MediaType type) {}
