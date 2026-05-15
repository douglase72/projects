package com.erdouglass.common.api.rest;

import java.io.IOException;
import java.util.Optional;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {
    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class);
    
  @Override
  public void filter(ContainerRequestContext request) throws IOException {
    LOGGER.infof("Request: %s %s", request.getMethod(), request.getUriInfo().getRequestUri());
    request.getHeaders().forEach((k, v) -> LOGGER.debugf("%s: %s", k, v));
    var sctx = request.getSecurityContext();
    if (sctx != null && sctx.getUserPrincipal() != null) {
        LOGGER.infof("User: %s", sctx.getUserPrincipal().getName());
    }
  }
            
  @Override
  public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
    var statusCode = response.getStatus();
    var phrase = Optional.ofNullable(Response.Status.fromStatusCode(statusCode))
        .map(s -> s.getReasonPhrase())
        .orElseGet(() -> ResponseStatus.from(statusCode).getReasonPhrase());
    LOGGER.infof("Response: %d (%s)", statusCode, phrase);
    var headers = response.getHeaders();
    headers.forEach((k, v) -> LOGGER.debugf("%s: %s", k, v));
  }
}