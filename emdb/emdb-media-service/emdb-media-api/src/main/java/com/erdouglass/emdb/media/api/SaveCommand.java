package com.erdouglass.emdb.media.api;

public sealed interface SaveCommand permits SaveMovie, SaveSeries, SavePerson {

  Integer tmdbId();
}
