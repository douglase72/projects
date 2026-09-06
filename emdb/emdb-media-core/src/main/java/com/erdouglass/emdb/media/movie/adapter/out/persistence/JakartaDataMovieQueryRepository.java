package com.erdouglass.emdb.media.movie.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;

import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import com.erdouglass.emdb.media.movie.application.port.out.MovieView;

@Repository(dataStore = "media")
interface JakartaDataMovieQueryRepository {

  @Query("""
      select m.publicId, m.version, m.title, m.releaseDate, m.score, m.originalLanguage, m.overview
      from MovieEntity m
      where m.publicId = :id          
            """)
    Optional<MovieView> findByPublicId(UUID id);
}
