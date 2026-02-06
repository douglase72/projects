package com.erdouglass.emdb.job.mapper;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;

import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.job.entity.IngestJob;

@ApplicationScoped
public class IngestStatusChangedMapper {
  
  public IngestJob toIngestJob(IngestStatusChanged event) {
    var job = new IngestJob();
    job.id(UUID.fromString(event.id()));
    job.emdbId(event.emdbId());
    job.tmdbId(event.tmdbId());
    job.timestamp(event.timestamp());
    job.source(event.source());
    job.type(event.type());
    job.status(event.status());
    job.name(event.name());
    return job;
  }
  
  public IngestStatusChanged toIngestStatusChanged(IngestJob job) {
    return IngestStatusChanged.builder()
        .id(job.id().toString())
        .emdbId(job.emdbId().orElse(null))
        .tmdbId(job.tmdbId())
        .timestamp(job.timestamp())
        .source(job.source())
        .type(job.type())
        .status(job.status()) 
        .name(job.name().orElse(null))
        .build();
  }

}
