package com.erdouglass.emdb.media.movie.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import com.erdouglass.emdb.media.movie.application.port.in.MovieCreditView;
import com.erdouglass.emdb.media.movie.application.port.in.MovieView;

@Repository(dataStore = "media")
interface JakartaDataMovieQueryRepository {

  @Query("""
      select m.id, m.version, m.title, m.releaseDate, m.score, m.originalLanguage, m.overview
      from MovieEntity m
      where m.id = :id          
            """)
    Optional<MovieView> findById(Long id);
  
  @Query("""
      select c.id, c.creditType, c.personId, c.name, c.role, c.order, c.department
      from MovieCreditEntity c
      where c.movie.id = :movieId
      order by c.creditType, c.order
      """)
  List<MovieCreditView> findCreditsByMovieId(Long movieId);
}
