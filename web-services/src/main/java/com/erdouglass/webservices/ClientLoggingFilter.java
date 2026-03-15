package com.erdouglass.webservices;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import jakarta.inject.Inject;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.ext.Provider;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentelemetry.api.trace.Span;

@Provider
public class ClientLoggingFilter implements ClientRequestFilter, ClientResponseFilter {
  private static final Logger LOGGER = LoggerFactory.getLogger(ClientLoggingFilter.class);
  
  @Inject
  @ConfigProperty(name = "logging.client.excludes")
  Optional<List<String>> excludes;
  
  @Override
  public void filter(ClientRequestContext request) throws IOException {
    if (isExcluded(request)) {
      return;
    }
    
    LOGGER.info("Request: {} {}", request.getMethod(), request.getUri());
    request.getHeaders().forEach((k,v) -> LOGGER.debug("{}: {}", k, v));
  }

  @Override
  public void filter(ClientRequestContext request, ClientResponseContext response) throws IOException {
    if (isExcluded(request)) {
      return;
    }
    
    LOGGER.info("Response: {} ({})", response.getStatus(), response.getStatusInfo().getReasonPhrase());
    if (Objects.nonNull(response.getHeaders())) {
        response.getHeaders().forEach((k,v) -> LOGGER.debug("{}: {}", k, v));
    }
    
    if (response.getStatus() >= 400) {
        var currentSpan = Span.current();
        currentSpan.setAttribute("error", true);
        currentSpan.setAttribute("http.status_code", response.getStatus());
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