package com.erdouglass.emdb.media.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.media.domain.SeriesService;
import com.erdouglass.emdb.media.series.SaveSeries;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Uni;

/// Consumes [SaveSeries] commands from the RabbitMQ save-movie queue.
@ApplicationScoped
public class SeriesConsumer extends Consumer<SaveSeries> {

  @Inject
  SeriesService service;

  @Override
  @RunOnVirtualThread
  @Incoming("save-series-in")
  public Uni<Void> onMessage(Message<SaveSeries> message) {
    return consume(message);
  }

  @Override
  protected void save(final SaveSeries command) {
    service.save(command);
  }
}
