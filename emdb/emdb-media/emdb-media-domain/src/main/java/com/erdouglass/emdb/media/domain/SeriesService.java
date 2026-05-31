package com.erdouglass.emdb.media.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.erdouglass.emdb.media.series.SaveSeries;
import com.erdouglass.emdb.media.series.SeriesResponse;

public interface SeriesService {

  SeriesResponse save(@NotNull @Valid SaveSeries command);
}
