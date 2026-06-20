package com.erdouglass.emdb.ingest.core;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.ingest.ws.rest.TmdbImageClient;
import com.erdouglass.emdb.media.Image;

@ApplicationScoped
public class TmdbImageScraper {

  @Inject
  @RestClient
  TmdbImageClient client;
  
  public Image scrape(String name) {
    if (name == null) {
      return null;
    }    
    try (var is = client.findByName(name)) {
      return new Image(UUID.randomUUID(), is.readAllBytes());
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to extract image: " + name, e);
    }
  }
}
