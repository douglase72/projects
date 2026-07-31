package com.erdouglass.emdb.ingest.adapter.outbound.tmdb;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.ingest.application.port.outbound.MediaSource;
import com.erdouglass.emdb.ingest.application.port.outbound.MovieDto;
import com.erdouglass.emdb.media.SourceId;

@ApplicationScoped
class TmdbMovieAdapter implements MediaSource {

  @Inject
  @RestClient
  TmdbClient client;

  @Override
  public MovieDto extract(SourceId id) {
    // TODO Auto-generated method stub
    return null;
  }
}
