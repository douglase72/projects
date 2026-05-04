package com.erdouglass.emdb.scheduler.messaging;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import com.erdouglass.emdb.common.api.MediaType;
import com.erdouglass.emdb.common.api.command.IngestMedia;
import com.erdouglass.emdb.common.api.command.IngestMedia.IngestSource;

import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.RunOnVirtualThread;

@ApplicationScoped
public class TmdbPersonScheduler extends TmdbScheduler {

  @Override
  @RunOnVirtualThread
  @Scheduled(cron = "{emdb.person.scheduler}")
  public void execute() {
    var changedPeople = List.of(13918, 12073);
    for (var tmdbId : changedPeople) {
      ingest(IngestMedia.of(tmdbId, MediaType.PERSON, IngestSource.SCHEDULER));
    } 
  }

  @Override
  public MediaType type() {
    return MediaType.PERSON;
  }
}
