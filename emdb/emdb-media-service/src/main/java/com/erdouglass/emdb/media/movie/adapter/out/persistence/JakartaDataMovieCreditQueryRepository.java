package com.erdouglass.emdb.media.movie.adapter.out.persistence;

import java.util.List;
import java.util.UUID;

import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import com.erdouglass.emdb.media.movie.application.port.in.MovieCreditView;

@Repository
interface JakartaDataMovieCreditQueryRepository {

  @Query("""
      select c.id, c.creditType, c.personId, c.name, c.role, c.order, c.department
      from MovieCreditEntity c
      where c.movie.id = :movieId
      order by c.creditType, c.order
      """)
  List<MovieCreditView> findByMovieId(UUID movieId);
}
