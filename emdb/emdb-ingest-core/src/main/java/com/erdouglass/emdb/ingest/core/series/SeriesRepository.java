package com.erdouglass.emdb.ingest.core.series;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;

@Repository(dataStore = "ingest")
interface SeriesRepository extends CrudRepository<Series, Integer> {

}
