package com.erdouglass.webservices;

import jakarta.enterprise.context.ApplicationScoped;

import org.jboss.logging.Logger;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.ForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.quarkus.grpc.GlobalInterceptor;

@GlobalInterceptor 
@ApplicationScoped
public class ClientLoggingInterceptor implements ClientInterceptor {
  private static final Logger LOGGER = Logger.getLogger(ClientLoggingInterceptor.class);

  @Override
  public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
      MethodDescriptor<ReqT, RespT> method,
      CallOptions callOptions, 
      Channel next) {
    LOGGER.infof("Request: %s", method.getFullMethodName());
    return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
      
      @Override
      public void start(Listener<RespT> responseListener, Metadata headers) {
        LOGGER.debugf("Headers: %s", headers);
        Listener<RespT> forwardingListener = new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {
          
          @Override
          public void onHeaders(Metadata responseHeaders) {
            LOGGER.debugf("Headers: %s", responseHeaders);
            super.onHeaders(responseHeaders);
          }

          @Override
          public void onClose(Status status, Metadata trailers) {
            String description = status.getDescription() != null ? status.getDescription() : "Success";
            LOGGER.infof("Response: %s (%s)", status.getCode(), description);
            LOGGER.debugf("Trailers: %s", trailers);
            super.onClose(status, trailers);
          }
        };
        super.start(forwardingListener, headers);
      }
    };
  }

}
