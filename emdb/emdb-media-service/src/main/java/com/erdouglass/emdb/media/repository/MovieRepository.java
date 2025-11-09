package com.erdouglass.emdb.media.repository;

import java.util.Optional;

import com.erdouglass.emdb.media.entity.Movie;

import jakarta.data.repository.Delete;
import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Update;

/// The Jakarta Data repository interface for the `Movie` entity.
///
/// This interface defines the contract for all database-level CRUD operations.
/// By annotating this interface with `@Repository`, Quarkus and the underlying
/// Jakarta Data provider (e.g., Hibernate) will automatically generate the
/// implementation for these methods at build time.
///
/// It uses standard Jakarta Data lifecycle annotations like `@Insert`, `@Find`,
/// `@Update`, and `@Delete` to declare the intended operation.
///
/// @see com.erdouglass.emdb.media.entity.Movie
/// @see com.erdouglass.emdb.media.service.MovieService
@Repository
public interface MovieRepository {
	
	@Insert
	Movie insert(Movie movie);
	
	@Find
	Optional<Movie> findById(Long id);
	
	@Update
	Movie update(Movie movie);
	
  @Delete
  void deleteById(Long id);

}
