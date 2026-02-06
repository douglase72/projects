package com.erdouglass.emdb.scraper.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.scraper.client.TmdbImageClient;

@ApplicationScoped
public class TmdbImageService {
  private static final Logger LOGGER = Logger.getLogger(TmdbImageService.class);
  
  @Inject
  @RestClient
  TmdbImageClient imageClient;  
  
  @Inject
  @ConfigProperty(name = "emdb.image.data")
  String imageData;  
  
  public UUID save(@NotNull String image) {
    if (image == null) return null;
    var emdbImage = UUID.randomUUID();
    
    try {
      var bucket = emdbImage.toString().substring(0, 2);
      try (var is = imageClient.findByName(image)) {
        var dir = Path.of(imageData, Configuration.ORIGINAL, bucket);
        Files.createDirectories(dir);
        var dest = dir.resolve(String.format("%s.jpg", emdbImage));
        Files.copy(is, dest, StandardCopyOption.REPLACE_EXISTING);
      }
    } catch (Exception e) {
      var msg = String.format("Failed to save image: %s\"", image);
      throw new RuntimeException(msg, e);
    }
    return emdbImage;
  }
  
  public void delete(@NotNull UUID image) {
    var dir = Path.of(imageData, Configuration.ORIGINAL, image.toString().substring(0, 2));
    var file = dir.resolve(String.format("%s.jpg", image));
    try {
      Files.deleteIfExists(file);
    } catch (IOException e) {
      LOGGER.errorf("Failed to delete image: %s", file, e);
    }    
  }

}
