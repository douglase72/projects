package com.erdouglass.emdb.media.core.movie;

import java.util.List;
import java.util.UUID;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

@Repository(dataStore = "media")
interface MovieCreditRepository extends CrudRepository<MovieCredit, UUID> {

  @Query("""
      SELECT c FROM MovieCredit c
      JOIN FETCH c.person
      WHERE c.movie.id = :movieId
      ORDER BY c.order
      """)
  List<MovieCredit> findByMovieId(Long movieId);

  @Query("delete from MovieCredit where movie = :movie")
  void deleteByMovie(Movie movie);
}
