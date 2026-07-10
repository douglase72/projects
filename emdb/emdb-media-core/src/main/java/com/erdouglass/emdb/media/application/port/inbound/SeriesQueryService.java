package com.erdouglass.emdb.media.application.port.inbound;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface SeriesQueryService {

  SeriesView findById(@NotNull @Positive Long id);
}
