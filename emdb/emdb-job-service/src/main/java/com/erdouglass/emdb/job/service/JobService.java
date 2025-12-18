package com.erdouglass.emdb.job.service;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.message.JobMessage;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;

@ApplicationScoped
public class JobService {
  private static final Logger LOGGER = Logger.getLogger(JobService.class);
  
  private final BroadcastProcessor<JobMessage> broadcaster = BroadcastProcessor.create();
  
  @RunOnVirtualThread
  @Incoming("job-log-in")
  public void onMessage(JobMessage message) {
    LOGGER.infof("[%d%%] %s, %s, %s", 
        message.progress(), message.status(), message.source(), message.content());
    broadcaster.onNext(message);
  }
  
  public Multi<JobMessage> stream(String jobId) {
    return broadcaster
        .onOverflow().buffer(256) 
        .filter(m -> m.id().equals(jobId));
  }
  
}
