package com.erdouglass.emdb.media.application.port.inbound.series;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface DeleteSeriesUseCase {

  void deleteById(@NotNull @Positive Long id);
}
