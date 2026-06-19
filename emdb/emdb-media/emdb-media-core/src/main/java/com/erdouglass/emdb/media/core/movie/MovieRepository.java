package com.erdouglass.emdb.media.core.movie;

import java.util.Optional;

import jakarta.data.Sort;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Find;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

@Repository
interface MovieRepository extends CrudRepository<Movie, Long> {
  
  @Find
  Page<Movie> findAll(PageRequest pageRequest, Sort<Movie> sort);

  @Query("WHERE tmdbId = :tmdbId")
  Optional<Movie> findByTmdbId(Integer tmdbId);
}
