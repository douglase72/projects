package com.erdouglass.emdb.media.series;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface SeriesCommandService {
  SeriesDto save(@NotNull @Valid SaveSeries command);
  
  SeriesDto update(@NotNull @Valid UpdateSeries command);
  
  void delete(@NotNull @Positive Long id);
}
