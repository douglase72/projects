package com.erdouglass.emdb.job.service;

import java.time.Duration;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.message.JobMessage;
import com.erdouglass.emdb.job.mapper.JobMapper;
import com.erdouglass.emdb.job.repository.JobRepository;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;

@ApplicationScoped
public class JobService {
  private static final Logger LOGGER = Logger.getLogger(JobService.class);
  private static final int BUFFER_SIZE = 256;
  private static final int HEARTBEAT = 60;
  
  private final BroadcastProcessor<JobMessage> broadcaster = BroadcastProcessor.create();
  
  @Inject
  JobMapper mapper;
  
  @Inject
  JobRepository repository;
  
  @Transactional
  @RunOnVirtualThread
  @Incoming("job-log-in")
  public void onMessage(JobMessage message) {
    var log = mapper.toJobLog(message);
    repository.insert(log);
    LOGGER.infof("%s, [%d%%] %s, %s, %s", log.jobId(), log.progress(), log.status(), log.source(), log.content());
    broadcaster.onNext(message);
  }
  
  public Multi<JobMessage> findAll() {
    var historyStream = Uni.createFrom().item(() -> repository.findAll())
        .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
        .onItem().transformToMulti(list -> Multi.createFrom().iterable(list))
        .map(mapper::toJobMessage);
    var liveStream = Multi.createFrom().publisher(broadcaster)
        .onOverflow().buffer(BUFFER_SIZE);
    var heartbeatStream = Multi.createFrom().ticks().every(Duration.ofSeconds(HEARTBEAT))
        .map(_ -> JobMessage.builder()
            .id(UUID.randomUUID())
            .tmdbId(999999999)
            .status(JobMessage.JobStatus.HEARTBEAT)
            .source(JobMessage.JobSource.GATEWAY)
            .content("Keep-Alive")
            .progress(0)
            .build());
    var persistentStream = Multi.createBy().merging().streams(liveStream, heartbeatStream);
    return Multi.createBy().concatenating()
        .streams(historyStream, persistentStream); 
  }
  
  public Multi<JobMessage> findById(UUID jobId) {
    var historyStream = Uni.createFrom().item(jobId)
        .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
        .map(id -> repository.findById(id)) 
        .onItem().transformToMulti(list -> Multi.createFrom().iterable(list))
        .map(mapper::toJobMessage);
    var liveStream = Multi.createFrom().publisher(broadcaster)
        .onOverflow().buffer(BUFFER_SIZE)
        .filter(job -> job.id().equals(jobId));
    return Multi.createBy().concatenating()
        .streams(historyStream, liveStream); 
  }
  
}
