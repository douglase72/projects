package com.erdouglass.emdb.gateway.controller;

import java.time.Duration;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.erdouglass.emdb.common.event.IngestStatusChanged;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;

/// In-process broadcaster for ingest status events.
///
/// Subscribes to the `status-events-in` channel and republishes each event
/// to all currently-connected SSE clients via a [BroadcastProcessor].
/// Slow clients drop older events rather than block the broadcaster.
/// A keepalive comment is emitted every 60 seconds to prevent idle
/// connections from being closed by intermediaries.
@ApplicationScoped
public class IngestService {
  private final BroadcastProcessor<IngestStatusChanged> broadcaster = BroadcastProcessor.create();
  
  @Context
  Sse sse;
  
  /// Forwards an inbound status event to all SSE subscribers.
  ///
  /// @param event the event received from the broker
  @Incoming("status-events-in")
  public void onMessage(IngestStatusChanged event) {
    broadcaster.onNext(event);
  }
  
  /// Returns a stream of SSE events for one client.
  ///
  /// The returned [Multi] completes only when the client disconnects.
  /// Heartbeat comments are interleaved with real events to keep the
  /// connection open through proxies and load balancers.
  ///
  /// @return a Multi of outbound SSE events for the calling client
  public Multi<OutboundSseEvent> stream() {
    Multi<OutboundSseEvent> events = broadcaster
        .onOverflow().dropPreviousItems()
        .map(event -> sse.newEventBuilder()
            .mediaType(MediaType.APPLICATION_JSON_TYPE)
            .data(IngestStatusChanged.class, event)
            .build());
    Multi<OutboundSseEvent> heartbeat = Multi.createFrom()
        .ticks().every(Duration.ofSeconds(60))
        .map(_-> sse.newEventBuilder().comment("keepalive").build());
    return Multi.createBy().merging().streams(events, heartbeat); 
  }
}
