package com.erdouglass.emdb.ingest.core;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

import jakarta.inject.Inject;

import com.erdouglass.emdb.ingest.core.image.ImageScraper;
import com.erdouglass.emdb.media.image.Image;

public abstract class Scraper<T> {
  
  @Inject
  ImageScraper imageScraper;
  
  protected Image resolveImage(
      T existing, 
      String newPath,
      Function<T, String> tmdbPathOf, 
      Function<T, UUID> emdbNameOf) {
    if (existing == null || !Objects.equals(tmdbPathOf.apply(existing), newPath)) {
      return imageScraper.scrape(newPath);
    }
    return new Image(emdbNameOf.apply(existing), null);
  } 
  
  protected static UUID nameOf(Image image) {
    return image == null ? null : image.name();
  }  
}
