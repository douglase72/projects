package com.erdouglass.emdb.job.controller;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.job.mapper.IngestStatusChangedMapper;
import com.erdouglass.emdb.job.repository.IngestJobRepository;

@Path("/jobs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JobResource {
  
  @Inject
  IngestStatusChangedMapper mapper;
  
  @Inject
  IngestJobRepository repository;
  
  @GET
  @Path("/ingest")
  public List<IngestStatusChanged> findAll() {
    return repository.findAll().stream()
        .map(mapper::toIngestStatusChanged)
        .toList();
  }
  
}
