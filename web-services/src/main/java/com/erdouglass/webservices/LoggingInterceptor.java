package com.erdouglass.webservices;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.Json;
import jakarta.json.JsonObject;

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
  private static final Metadata.Key<String> AUTHORIZATION;
  private static final Logger LOGGER = Logger.getLogger(LoggingInterceptor.class);
  
  static {
    AUTHORIZATION = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
  }
  
  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
      ServerCall<ReqT, RespT> call, 
      Metadata headers, 
      ServerCallHandler<ReqT, RespT> next) {
    String methodName = call.getMethodDescriptor().getFullMethodName();
    LOGGER.infof("Request: %s", methodName);
    
    String user = extractUsername(headers);
    if (user != null) {
        LOGGER.infof("User: %s", user);
    }   

    ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT> forwardingCall =
      new ForwardingServerCall.SimpleForwardingServerCall<>(call) {      
        @Override
        public void close(Status status, Metadata trailers) {
          String description = status.getDescription() != null ? status.getDescription() : "Success";
          LOGGER.infof("Response: %s (%s)", status.getCode(), description);
          super.close(status, trailers);
        }
      };
    var listener = next.startCall(forwardingCall, headers);

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
  
  private static String extractUsername(Metadata headers) {
    String auth = headers.get(AUTHORIZATION);
    if (auth == null || !auth.startsWith("Bearer ")) {
      return null;
    }
    try {
      String[] parts = auth.substring("Bearer ".length()).split("\\.");
      if (parts.length < 2)
        return null;
      String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
      try (var reader = Json.createReader(new StringReader(payload))) {
        JsonObject claims = reader.readObject();
        if (claims.containsKey("preferred_username")) {
          return claims.getString("preferred_username");
        }
        if (claims.containsKey("sub")) {
          return claims.getString("sub");
        }
        return null;
      }
    } catch (Exception e) {
      LOGGER.debugf("Could not parse JWT for logging: %s", e.getMessage());
      return null;
    }
  }
}
