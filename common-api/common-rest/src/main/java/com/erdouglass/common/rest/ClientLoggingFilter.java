package com.erdouglass.common.rest;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.ext.Provider;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@Provider
@ApplicationScoped
public class ClientLoggingFilter implements ClientRequestFilter, ClientResponseFilter {
  private static final Logger LOGGER = Logger.getLogger(ClientLoggingFilter.class);
  
  @Inject
  @ConfigProperty(name = "logging.client.excludes")
  Optional<List<String>> excludes;
  
  @Override
  public void filter(ClientRequestContext request) throws IOException {
    if (isExcluded(request)) {
      return;
    }    
    LOGGER.infof("Request: %s %s", request.getMethod(), request.getUri());
    request.getHeaders().forEach((k,v) -> LOGGER.debugf("%s: %s", k, v));
  }

  @Override
  public void filter(ClientRequestContext request, ClientResponseContext response) throws IOException {
    if (isExcluded(request)) {
      return;
    }    
    LOGGER.infof("Response: %s (%s)", response.getStatus(), response.getStatusInfo().getReasonPhrase());
    if (Objects.nonNull(response.getHeaders())) {
        response.getHeaders().forEach((k,v) -> LOGGER.debugf("%s: %s", k, v));
    }
  }
  
  private boolean isExcluded(ClientRequestContext requestContext) {
    if (excludes == null) return false;
    String requestUri = requestContext.getUri().toString();
    return excludes.map(prefixes -> prefixes.stream()
        .filter(prefix -> prefix != null && !prefix.isBlank())
        .anyMatch(prefix -> requestUri.startsWith(prefix))
    ).orElse(false); //
  }  
}
