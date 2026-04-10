package com.erdouglass.webservices;

import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintViolationException;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.Status;
import io.quarkus.grpc.ExceptionHandler;
import io.quarkus.grpc.ExceptionHandlerProvider;

@ApplicationScoped
public class GrpcExceptionHandler implements ExceptionHandlerProvider {

  @Override
  public <ReqT, RespT> ExceptionHandler<ReqT, RespT> createHandler(
      ServerCall.Listener<ReqT> listener, 
      ServerCall<ReqT, RespT> serverCall, 
      Metadata metadata) {
    return new ExceptionHandler<ReqT, RespT>(listener, serverCall, metadata) {
      
      @Override
      protected void handleException(Throwable exception, ServerCall<ReqT, RespT> call, Metadata reqMetadata) {
        Throwable transformed = transform(exception);
        Status status = Status.fromThrowable(transformed);
        Metadata trailers = Status.trailersFromThrowable(transformed);
        call.close(status, trailers != null ? trailers : new Metadata());
      }
    };
  }

  @Override
  public Throwable transform(Throwable t) {
    if (t instanceof DateTimeParseException) {
      return Status.INVALID_ARGUMENT
          .withDescription("Invalid release_date format. Expected YYYY-MM-DD")
          .asRuntimeException();
    }

    if (t instanceof ConstraintViolationException cve) {
      String errorMsg = cve.getConstraintViolations().stream()
          .map(v -> v.getPropertyPath() + " " + v.getMessage())
          .collect(Collectors.joining(", "));          
      return Status.INVALID_ARGUMENT
          .withDescription("Validation failed: " + errorMsg)
          .asRuntimeException();
    }
    
    if (t instanceof ResourceNotFoundException rnfe) {
      return Status.NOT_FOUND
          .withDescription(rnfe.getMessage())
          .asRuntimeException();      
    }
    return ExceptionHandlerProvider.toStatusException(t, true);
  }

}
