package com.erdouglass.emdb.ingest.core.movie;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;

@Repository(dataStore = "ingest")
public interface MovieRepository extends CrudRepository<Movie, Integer> {

}
