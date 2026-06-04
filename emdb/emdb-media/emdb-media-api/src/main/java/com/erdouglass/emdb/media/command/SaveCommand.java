package com.erdouglass.emdb.media.command;

public sealed interface SaveCommand permits SaveMovie, SaveSeries, SavePerson {

  Integer tmdbId();
  
  String homepage();
}
