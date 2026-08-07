package com.erdouglass.emdb.media.adapter.outbound.persistence.movie;

import java.util.List;

import jakarta.data.repository.Insert;
import jakarta.data.repository.Repository;

@Repository(dataStore = "media")
public interface JakartaDataMovieAuditRepository {

  @Insert
  void insertAll(List<MovieAuditEntity> entries);
}
