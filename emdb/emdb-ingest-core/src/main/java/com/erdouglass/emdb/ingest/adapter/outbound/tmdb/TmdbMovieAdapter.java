package com.erdouglass.emdb.ingest.adapter.outbound.tmdb;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.ingest.application.port.outbound.Movie;
import com.erdouglass.emdb.media.OriginalLanguage;
import com.erdouglass.emdb.media.ReleaseDate;
import com.erdouglass.emdb.media.SourceId;
import com.erdouglass.emdb.media.SourceId.Source;
import com.erdouglass.emdb.media.Title;

/// Extract movie details from TMDB.
@ApplicationScoped
class TmdbMovieAdapter {
  private static final String CREDITS = "credits";
  
  @Inject
  @RestClient
  TmdbClient client;

  public Movie extract(String tmdbId) {
    var tmdbMovie = client.findMovieById(Integer.valueOf(tmdbId), CREDITS);
    return Movie.builder()
        .sourceId(SourceId.of(Source.TMDB,  tmdbMovie.id().toString()))
        .title(Title.of(tmdbMovie.title()))
        .releaseDate(ReleaseDate.from(tmdbMovie.release_date()).orElse(null))
        .originalLanguage(OriginalLanguage.of(tmdbMovie.original_language()))
        .build();
  }
}
