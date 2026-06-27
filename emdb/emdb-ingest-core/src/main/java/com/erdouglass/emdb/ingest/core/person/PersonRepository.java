package com.erdouglass.emdb.ingest.core.person;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;

@Repository(dataStore = "ingest")
interface PersonRepository extends CrudRepository<Person, Integer> {

}
