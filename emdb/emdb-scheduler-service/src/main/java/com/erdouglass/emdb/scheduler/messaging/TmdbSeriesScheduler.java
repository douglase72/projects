package com.erdouglass.emdb.scheduler.messaging;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import com.erdouglass.emdb.common.api.MediaType;
import com.erdouglass.emdb.common.api.command.IngestMedia;
import com.erdouglass.emdb.common.api.command.IngestMedia.IngestSource;

import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.RunOnVirtualThread;

@ApplicationScoped
public class TmdbSeriesScheduler extends TmdbScheduler {

  @Override
  @RunOnVirtualThread
  @Scheduled(cron = "{emdb.series.scheduler}")
  public void execute() {
    var changedSeries = List.of(113959, 4614, 456);
    for (var tmdbId : changedSeries) {
      ingest(IngestMedia.of(tmdbId, MediaType.SERIES, IngestSource.SCHEDULER));
    }   
  }

  @Override
  public MediaType type() {
    return MediaType.SERIES;
  }
}
