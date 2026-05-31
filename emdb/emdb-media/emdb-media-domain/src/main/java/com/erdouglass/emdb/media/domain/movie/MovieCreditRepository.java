package com.erdouglass.emdb.media.domain.movie;

import java.util.UUID;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

@Repository
interface MovieCreditRepository extends CrudRepository<MovieCredit, UUID> {

  @Query("delete from MovieCredit where movie = :movie")
  void deleteByMovie(Movie movie);
}
