package com.erdouglass.emdb.media.movie;

import java.util.List;
import java.util.UUID;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

/// Jakarta Data repository for [MovieCredit] persistence, adding bulk removal of
/// every credit attached to a given movie.
@Repository
interface CreditRepository extends CrudRepository<MovieCredit, UUID> {
  
  @Query("""
      SELECT c FROM MovieCredit c
      JOIN FETCH c.person
      WHERE c.movie.id = :movieId
      """)
  List<MovieCredit> findByMovieId(Long movieId);  

  /// Deletes all credits associated with the given movie.
  ///
  /// @param movie the movie whose credits should be removed
  @Query("delete from MovieCredit where movie = :movie")
  void deleteByMovie(Movie movie);
}
