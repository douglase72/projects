package com.erdouglass.emdb.job.mapper;

import jakarta.enterprise.context.ApplicationScoped;

import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.job.entity.IngestJob;
import com.erdouglass.emdb.job.query.IngestJobDto;

@ApplicationScoped
public class IngestJobMapper {
  
  public IngestJob toIngestJob(IngestStatusChanged event) {
    var job = new IngestJob();
    job.id(event.id());
    job.status(event.status(), event.timestamp(), event.source());
    job.emdbId(event.emdbId());
    job.tmdbId(event.tmdbId());
    job.type(event.type());
    job.name(event.name());
    return job;
  }
  
  public IngestJobDto toIngestJobDto(IngestJob job) {
    return IngestJobDto.builder()
        .id(job.id())
        .timestamp(job.modified())
        .status(job.status())
        .source(job.source())
        .emdbId(job.emdbId().orElse(null))
        .tmdbId(job.tmdbId())
        .type(job.type())
        .name(job.name().orElse(null))
        .history(job.history())
        .build();   
  }

}
