package com.erdouglass.emdb.media.consumer;

import java.util.concurrent.CompletionStage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.common.command.SeriesCreateCommand;
import com.erdouglass.emdb.media.mapper.SeriesMapper;
import com.erdouglass.emdb.media.service.SeriesService;

import io.smallrye.reactive.messaging.annotations.Blocking;

@ApplicationScoped
public class SeriesConsumer extends Consumer<SeriesCreateCommand> {
  
  @Inject
  SeriesMapper seriesMapper;
  
  @Inject
  SeriesService seriesService;
  
  @Blocking
  @Incoming("series")
  public CompletionStage<Void> consume(Message<SeriesCreateCommand> message) {
    return super.consume(message);
  }
  
  @Override
  protected void process(Message<SeriesCreateCommand> message) {
    seriesService.create(seriesMapper.toSeries(message.getPayload()));
  }
  
}
