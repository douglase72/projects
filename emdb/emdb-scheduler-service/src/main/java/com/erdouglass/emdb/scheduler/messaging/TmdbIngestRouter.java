package com.erdouglass.emdb.scheduler.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.erdouglass.emdb.scheduler.api.command.ExecuteScheduler;

@ApplicationScoped
public class TmdbIngestRouter {
  
  @Inject 
  Instance<TmdbScheduler> schedulers;
  
  @Incoming("execute-scheduler-in")
  public void executeNow(ExecuteScheduler command) {
    schedulers.stream()
        .filter(s -> s.type() == command.type())
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(
            "No scheduler for type " + command.type()))
        .execute();
  }  
}
