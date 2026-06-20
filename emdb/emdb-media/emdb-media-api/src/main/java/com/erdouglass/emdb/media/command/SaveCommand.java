package com.erdouglass.emdb.media.command;

public sealed interface SaveCommand permits SaveMovie {

  Integer tmdbId();
  
  String homepage();
}
