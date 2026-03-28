package com.erdouglass.emdb.scraper.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.scraper.client.TmdbImageClient;
import com.fasterxml.uuid.Generators;

@ApplicationScoped
public class TmdbImageService {
  
  @Inject
  @ConfigProperty(name = "emdb.image.data")
  String imageData;
  
  @Inject
  @RestClient
  TmdbImageClient imageClient;  
  
  public UUID save(@NotNull String image) {
    var emdbImage = Generators.timeBasedEpochGenerator().generate(); 
    var hex = emdbImage.toString();
    var bucket = hex.substring(hex.length() - 2);
    try (var is = imageClient.findByName(image)) {
      var dir = Path.of(imageData, Configuration.ORIGINAL, bucket);
      Files.createDirectories(dir);
      var dest = dir.resolve(String.format("%s.jpg", emdbImage));
      Files.copy(is, dest, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to save image: " + image, e);
    }
    return emdbImage;
  }
  
  public void delete(@NotNull UUID image) {
    var hex = image.toString();
    var bucket = hex.substring(hex.length() - 2);
    try {
      var dir = Path.of(imageData, Configuration.ORIGINAL, bucket);
      var file = dir.resolve(String.format("%s.jpg", image));
      Files.deleteIfExists(file);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to delete image: " + image, e);
    }    
  }

}
