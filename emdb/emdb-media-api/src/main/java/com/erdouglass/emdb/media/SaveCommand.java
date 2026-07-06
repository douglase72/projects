package com.erdouglass.emdb.media;

public sealed interface SaveCommand permits SaveMovie, SavePerson, SaveSeries {

  Integer tmdbId();
}
