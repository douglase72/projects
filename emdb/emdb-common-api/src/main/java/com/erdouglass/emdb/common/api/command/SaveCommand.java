package com.erdouglass.emdb.common.api.command;

public sealed interface SaveCommand permits SaveMovie {

  Integer tmdbId();
}
