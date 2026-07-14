package com.erdouglass.emdb.media.application.port.inbound.series;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface UpdateSeriesUseCase {

  SeriesView update(@NotNull @Positive Long id, @NotNull @Valid UpdateSeries command);
}
