package com.erdouglass.common.rest;

import java.io.IOException;
import java.util.zip.GZIPInputStream;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.ReaderInterceptorContext;

@Provider
public class GzipReaderInterceptor implements ReaderInterceptor {

  @Override
  public Object aroundReadFrom(ReaderInterceptorContext ctx) throws IOException, WebApplicationException {
    var encoding = ctx.getHeaders().getFirst("Content-Encoding");
    if ("gzip".equalsIgnoreCase(encoding)) {
      ctx.setInputStream(new GZIPInputStream(ctx.getInputStream()));
    }
    return ctx.proceed();
  }  
}
