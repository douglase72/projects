package com.erdouglass.emdb.media.series;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.erdouglass.emdb.media.series.SeriesDto.SeriesCredits;

public interface SeriesQueryService {

  SeriesCredits findCreditsBySeriesId(@NotNull @Positive Long id);
  
  SeriesDto findById(@NotNull @Positive Long id);
}
