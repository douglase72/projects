package com.erdouglass.emdb.media.adapter.outbound.movie;

import java.util.Optional;

import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import com.erdouglass.emdb.media.adapter.inbound.movie.MovieView;

@Repository(dataStore = "media")
public interface JakartaDataMovieQueryRepository {

  @Query("""
    select m.id, m.version, m.title, m.releaseDate, m.score, m.originalLanguage
    from MovieEntity m
    where m.id = :id          
          """)
  Optional<MovieView> findById(Long id);
}
