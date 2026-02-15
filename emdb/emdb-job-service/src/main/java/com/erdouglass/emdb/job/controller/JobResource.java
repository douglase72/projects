package com.erdouglass.emdb.job.controller;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import com.erdouglass.emdb.job.query.IngestJobDto;
import com.erdouglass.emdb.job.service.IngestJobService;

@Path("/jobs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JobResource {

  @Inject
  IngestJobService service;
  
  @GET
  @Path("/ingest")
  public List<IngestJobDto> findAll() {
    return service.findAll();
  }
  
}
