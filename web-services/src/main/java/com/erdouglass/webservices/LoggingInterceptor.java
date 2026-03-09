package com.erdouglass.webservices;

import jakarta.enterprise.context.ApplicationScoped;

import org.jboss.logging.Logger;

import io.grpc.ForwardingServerCall;
import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import io.quarkus.grpc.GlobalInterceptor;

@GlobalInterceptor 
@ApplicationScoped
public class LoggingInterceptor implements ServerInterceptor {
  private static final Logger LOGGER = Logger.getLogger(LoggingInterceptor.class);
  
  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
      ServerCall<ReqT, RespT> call, 
      Metadata headers, 
      ServerCallHandler<ReqT, RespT> next) {

    String methodName = call.getMethodDescriptor().getFullMethodName();
    LOGGER.infof("Request: %s", methodName);

    ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT> forwardingCall =
        new ForwardingServerCall.SimpleForwardingServerCall<>(call) {
      
          @Override
          public void close(Status status, Metadata trailers) {
            String description = status.getDescription() != null ? status.getDescription() : "Success";
            LOGGER.infof("Response: %s (%s)", status.getCode(), description);
            super.close(status, trailers);
          }
        };
    ServerCall.Listener<ReqT> listener = next.startCall(forwardingCall, headers);

    return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(listener) {
      @Override
      public void onCancel() {
        LOGGER.warnf("Request Cancelled: %s", methodName);
        super.onCancel();
      }
      
      @Override
      public void onHalfClose() {
        super.onHalfClose();
      }
    };
  }
  
}
