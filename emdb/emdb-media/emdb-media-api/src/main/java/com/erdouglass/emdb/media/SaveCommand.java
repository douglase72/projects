package com.erdouglass.emdb.media;

public sealed interface SaveCommand permits SaveMovie, SaveSeries, SavePerson {

  Integer tmdbId();
  
  String homepage();
}
