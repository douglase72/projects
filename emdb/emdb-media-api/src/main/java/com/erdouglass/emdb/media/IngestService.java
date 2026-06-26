package com.erdouglass.emdb.media;

import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface IngestService {

  UUID publish(@NotNull @Valid IngestMedia command);
}
