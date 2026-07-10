package com.erdouglass.emdb.media.application.port.inbound;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface SeriesCommandService {

  SeriesView update(@NotNull @Valid UpdateSeries command);
  
  void deleteById(@NotNull @Positive Long id);
}
