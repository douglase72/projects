package com.erdouglass.emdb.scheduler.messaging;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import com.erdouglass.emdb.common.api.MediaType;
import com.erdouglass.emdb.common.api.command.IngestMedia;
import com.erdouglass.emdb.common.api.command.IngestMedia.IngestSource;

import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.RunOnVirtualThread;

@ApplicationScoped
public class TmdbMovieScheduler extends TmdbScheduler {
  
  @Override
  @RunOnVirtualThread
  @Scheduled(cron = "{emdb.movie.scheduler}")  
  public void execute() {
    var changedMovies = List.of(335984, 818);
    for (var tmdbId : changedMovies) {
      ingest(IngestMedia.of(tmdbId, MediaType.MOVIE, IngestSource.SCHEDULER));
    }    
  }

  @Override
  public MediaType type() {
    return MediaType.MOVIE;
  }
}
