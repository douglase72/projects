package com.erdouglass.emdb.gateway.controller;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.scheduler.api.command.ExecuteScheduler;

import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@Path("/scheduler")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SchedulerResource {
  private static final String ROUTING_KEY = "execute.scheduler";
  
  @Inject
  @Channel("execute-scheduler-out")
  Emitter<ExecuteScheduler> emitter;  
  
  @POST
  public Response execute(@NotNull @Valid ExecuteScheduler command) {
    emitter.send(Message.of(command)
        .addMetadata(OutgoingRabbitMQMetadata.builder()
        .withRoutingKey(ROUTING_KEY)
        .withHeader("X-Event-Type", command.getClass().getSimpleName())
        .build()));
    return Response.status(Status.ACCEPTED).build();
  }
}
