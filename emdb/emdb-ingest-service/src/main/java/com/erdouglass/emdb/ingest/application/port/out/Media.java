package com.erdouglass.emdb.ingest.application.port.out;

public sealed interface Media permits Movie, Person {

  Integer tmdbId();
}
