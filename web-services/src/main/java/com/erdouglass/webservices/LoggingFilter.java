package com.erdouglass.webservices;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);
	
  @Override
  public void filter(ContainerRequestContext request) throws IOException {
    LOGGER.info("Request: {} {}", request.getMethod(), request.getUriInfo().getRequestUri());
    request.getHeaders().forEach((k, v) -> LOGGER.info("{}: {}", k, v));
    var sctx = request.getSecurityContext();
    if (sctx != null && sctx.getUserPrincipal() != null) {
    	LOGGER.info("User: {}", sctx.getUserPrincipal().getName());
    }
  }
			
  @Override
  public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
    var statusCode = response.getStatus();
    var phrase = Optional.ofNullable(Response.Status.fromStatusCode(statusCode))
        .map(s -> s.getReasonPhrase())
        .orElseGet(() -> ResponseStatus.from(statusCode).getReasonPhrase());
    LOGGER.info("Response: {} ({})", statusCode, phrase);
    var headers = response.getHeaders();
    headers.forEach((k, v) -> LOGGER.debug("{}: {}", k, v));
  }

}
