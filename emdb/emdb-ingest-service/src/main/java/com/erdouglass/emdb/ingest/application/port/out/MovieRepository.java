package com.erdouglass.emdb.ingest.application.port.out;

import com.erdouglass.emdb.ingest.domain.model.IngestId;

public interface MovieRepository {

  void save(IngestId id, Movie movie);
}
