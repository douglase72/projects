package com.erdouglass.emdb.media;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface SaveSeriesService {

  SaveResult save(@NotNull @Valid SaveSeries command);
}
