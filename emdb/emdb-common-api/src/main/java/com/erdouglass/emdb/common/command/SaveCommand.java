package com.erdouglass.emdb.common.command;

public sealed interface SaveCommand permits SaveMovie {

  Integer tmdbId();
}
