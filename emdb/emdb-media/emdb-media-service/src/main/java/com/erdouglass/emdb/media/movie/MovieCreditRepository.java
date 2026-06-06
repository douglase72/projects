package com.erdouglass.emdb.media.movie;

import java.util.UUID;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

/// Jakarta Data repository for [MovieCredit] persistence, adding bulk removal of
/// every credit attached to a given movie.
@Repository
interface MovieCreditRepository extends CrudRepository<MovieCredit, UUID> {

  /// Deletes all credits associated with the given movie.
  ///
  /// @param movie the movie whose credits should be removed
  @Query("delete from MovieCredit where movie = :movie")
  void deleteByMovie(Movie movie);
}
