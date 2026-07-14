package com.erdouglass.emdb.media.application.port.inbound.series;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.erdouglass.emdb.media.application.port.inbound.series.SeriesView.SeriesCredits;

public interface QuerySeriesUseCase {
  
  SeriesCredits findCreditsBySeriesId(@NotNull @Positive Long id);

  SeriesView findById(@NotNull @Positive Long id);
}
