package com.erdouglass.emdb.common;

import com.erdouglass.emdb.common.movie.SaveMovie;
import com.erdouglass.emdb.common.series.SaveSeries;

public sealed interface SaveCommand permits SaveMovie, SaveSeries {

  Integer tmdbId();
}
