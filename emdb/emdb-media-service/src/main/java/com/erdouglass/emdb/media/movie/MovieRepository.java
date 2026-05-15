package com.erdouglass.emdb.media.movie;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;

@Repository
interface MovieRepository extends CrudRepository<Movie, Long> {

}
