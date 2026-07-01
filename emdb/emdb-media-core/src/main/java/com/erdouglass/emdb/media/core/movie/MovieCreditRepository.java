package com.erdouglass.emdb.media.core.movie;

import java.util.List;
import java.util.UUID;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

@Repository(dataStore = "media")
public interface MovieCreditRepository extends CrudRepository<MovieCredit, UUID> {

  @Query("""
      SELECT c FROM MovieCredit c
      JOIN FETCH c.person
      WHERE c.movie.id = :movieId
      ORDER BY c.order
      """)
  List<MovieCredit> findByMovieId(Long movieId); 
  
  /// Finds a person's movie credits with the movie eagerly fetched, ordered by
  /// the movie's score descending.
  ///
  /// @param personId the person id
  /// @return the person's movie credits
  @Query("""
      SELECT mc FROM MovieCredit mc
      JOIN FETCH mc.movie m
      WHERE mc.person.id = :personId
      ORDER BY m.score DESC
      """)
  List<MovieCredit> findByPersonId(Long personId);

  @Query("delete from MovieCredit where movie = :movie")
  void deleteByMovie(Movie movie);
}
