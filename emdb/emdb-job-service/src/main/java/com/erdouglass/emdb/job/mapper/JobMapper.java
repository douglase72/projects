package com.erdouglass.emdb.job.mapper;

import jakarta.enterprise.context.ApplicationScoped;

import com.erdouglass.emdb.common.message.JobMessage;
import com.erdouglass.emdb.job.entity.JobLog;

@ApplicationScoped
public class JobMapper {
  
  public JobLog toJobLog(JobMessage message) {
    var log = new JobLog(message.id());
    log.timestamp(message.timestamp());
    log.source(message.source());
    log.status(message.status());
    log.content(message.content());
    log.progress(message.progress());
    return log;
  }
  
  public JobMessage toJobMessage(JobLog log) {
    return JobMessage.builder()
        .id(log.jobId())
        .timestamp(log.timestamp())
        .source(log.source())
        .status(log.status())
        .content(log.content())
        .progress(log.progress())
        .build();
  }

}
