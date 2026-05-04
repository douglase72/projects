package com.erdouglass.emdb.media.api.command;

public sealed interface SaveCommand permits SaveMovie, SaveSeries, SavePerson {
  
  Integer tmdbId();
}
