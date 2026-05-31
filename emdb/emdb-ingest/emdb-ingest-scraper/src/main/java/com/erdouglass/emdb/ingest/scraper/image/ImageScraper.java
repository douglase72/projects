package com.erdouglass.emdb.ingest.scraper.image;

import java.io.IOException;
import java.io.UncheckedIOException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.media.Image;

@ApplicationScoped
public class ImageScraper {
  
  @Inject
  @RestClient
  ImageClient client;
  
  public Image extract(final String name) {
    try (var is = client.findByName(name)) {
      return Image.builder()
          .tmdbName(name)
          .data(is.readAllBytes())
          .build();
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to extract image: " + name, e);
    }
  }
}
