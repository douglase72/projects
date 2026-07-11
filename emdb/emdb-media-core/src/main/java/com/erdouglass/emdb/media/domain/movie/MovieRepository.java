package com.erdouglass.emdb.media.domain.movie;

import java.util.List;
import java.util.Optional;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

@Repository(dataStore = "media")
public interface MovieRepository extends CrudRepository<Movie, Long> {

  /// Retrieves a movie by its corresponding external identifier.
  ///
  /// @param externalId the external identifier
  /// @return an [Optional] containing the movie if found, or empty if it does not exist
  @Query("WHERE externalId = :externalId")
  Optional<Movie> findByExternalId(Long externalId);
  
  @Insert
  List<MovieCredit> insertCredits(List<MovieCredit> credits);
  
  @Query("""
      SELECT NEW com.erdouglass.emdb.media.domain.movie.MovieCreditProjection(
          c.type, c.role, c.creditOrder, p.id, p.name, p.gender, p.profile)
      FROM MovieCredit c
      JOIN Person p ON p.id = c.personId
      WHERE c.movieId = :movieId
      ORDER BY c.creditOrder NULLS LAST, c.type
      """)
  List<MovieCreditProjection> findCreditsByMovieId(Long movieId);
  
  @Query("DELETE FROM MovieCredit WHERE movieId = :movieId")
  void deleteCreditsByMovieId(Long movieId);
}
