package com.erdouglass.emdb.gateway.controller;

import java.time.temporal.ChronoUnit;
import java.util.List;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.common.comand.UpdatePerson;
import com.erdouglass.emdb.common.query.PersonDetails;
import com.erdouglass.emdb.common.query.PersonView;
import com.erdouglass.emdb.gateway.mapper.PersonMapper;
import com.erdouglass.emdb.gateway.query.Page;
import com.erdouglass.emdb.gateway.query.PersonQueryParams;
import com.erdouglass.emdb.media.proto.v1.DeleteRequest;
import com.erdouglass.emdb.media.proto.v1.PersonServiceGrpc.PersonServiceBlockingStub;
import com.erdouglass.webservices.ResponseStatus;

import io.grpc.StatusRuntimeException;
import io.quarkus.grpc.GrpcClient;
import io.smallrye.common.annotation.RunOnVirtualThread;

/// REST resource for person operations.
///
/// Delegates to the media-service via gRPC. Write operations require the
/// admin role; read operations are public.
@Path("/people")
@RunOnVirtualThread
@RolesAllowed(Configuration.ADMIN)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Timeout(value = Configuration.GATEWAY_TIMEOUT, unit = ChronoUnit.SECONDS)
@CircuitBreaker(requestVolumeThreshold = 10, failureRatio = 0.5, delay = 10, delayUnit = ChronoUnit.SECONDS)
public class PersonResource {
  
  @Inject
  PersonMapper mapper;
  
  @GrpcClient("media-service")
  PersonServiceBlockingStub service;
  
  /// Creates a person if they do not already exist, otherwise updates the
  /// existing person matched by TMDB ID.
  ///
  /// @param command the person data to save
  /// @return 201 Created with [PersonDetails] if new, 200 OK if updated,
  ///         or 204 No Content if unchanged
  @POST
  public Response save(@NotNull @Valid SavePerson command) {
    var request = mapper.toSavePersonRequest(command);
    var response = service.save(request);    
    return Response.status(mapper.mapProtoStatusToHttpCode(response.getStatus()))
        .entity(mapper.toPersonDetails(response.getPerson()))
        .build();
  }
  
  /// Saves a batch of people in a single request.
  ///
  /// Each person is individually created or updated by TMDB ID using the
  /// same logic as [#save(SavePerson)]. The response contains a
  /// [MultiResponse] per person with its resulting HTTP status code.
  ///
  /// @param commands the list of people to save
  /// @return 207 Multi-Status with a [MultiResponse] for each person
  @POST
  @Path("/batch")
  public Response saveAll(List<@Valid SavePerson> commands) {
    var request = mapper.toSavePeopleRequest(commands);
    var results = service.saveAll(request);
    var response = mapper.toMultiResponse(results.getResultsList());
    return Response.status(ResponseStatus.MULTI_STATUS)
        .entity(response)
        .build();    
  }
  
  /// Returns a paginated list of [PersonView] projections.
  ///
  /// Results are sorted by name ascending by default where clients should use 
  /// [Page#hasNext()] to determine if more pages are available.
  ///
  /// @param parameters pagination and sorting options
  /// @return a [Page] of [PersonView] projections
  @PermitAll
  @GET
  @Retry(
      maxRetries = 3, delay = 200, delayUnit = ChronoUnit.MILLIS, jitter = 50,
      abortOn = StatusRuntimeException.class )
  public Page<PersonView> findAll(@BeanParam @Valid PersonQueryParams parameters) {
    var request = mapper.toFindAllPersonRequest(parameters);
    var response = mapper.toPage(service.findAll(request));
    return response;
  }
  
  /// Returns the full details of a single person.
  ///
  /// The optional `append` query parameter controls which associations
  /// are included in the response (e.g. `credits`).
  ///
  /// @param id the person's primary key
  /// @param append optional comma-separated list of associations to include
  /// @return the [PersonDetails] for the requested person
  @PermitAll
  @GET
  @Path("/{id}")
  @Retry(
      maxRetries = 3, delay = 200, delayUnit = ChronoUnit.MILLIS, jitter = 50,
      abortOn = StatusRuntimeException.class )
  public PersonDetails findById(
      @PathParam("id") @NotNull @Positive Long id, 
      @QueryParam(Configuration.APPEND) String append) {
    var request = mapper.toFindRequest(id, append);
    var response = service.findById(request);
    return mapper.toPersonDetails(response);
  }
  
  /// Updates an existing person by primary key with new field values.
  ///
  /// @param id the person's primary key
  /// @param command the fields to update
  /// @return the updated [PersonDetails]
  @PUT
  @Path("/{id}")
  public PersonDetails update(
      @PathParam("id") @NotNull @Positive Long id, 
      @NotNull @Valid UpdatePerson command) {
    var request = mapper.toUpdatePersonRequest(id, command);
    var response = service.update(request);
    return mapper.toPersonDetails(response);
  }
  
  /// Deletes a person by primary key.
  ///
  /// @param id the person's primary key
  /// @return 204 No Content on success
  @DELETE
  @Path("/{id}")
  @Retry(maxRetries = 3, delay = 200, delayUnit = ChronoUnit.MILLIS)
  public Response delete(@PathParam("id") @NotNull @Positive Long id) {
    var request = DeleteRequest.newBuilder().setId(id).build();
    service.delete(request);
    return Response.noContent().build();
  }
}
