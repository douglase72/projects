package com.erdouglass.emdb.media.image;

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

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;

@ApplicationScoped
public class ImageService {
  private static final String ORIGINAL = "original";
  
  private final NoArgGenerator GENERATOR = Generators.timeBasedEpochGenerator();
  
  @Inject
  @RestClient
  ImageClient client;
  
  @Inject
  @ConfigProperty(name = "emdb.image.data")
  String imageData;
  
  public UUID save(@NotNull String image) {
    var newImage = GENERATOR.generate();
    var hex = newImage.toString();
    var bucket = hex.substring(hex.length() - 2);
    try (var is = client.findByName(image)) {
      var dir = Path.of(imageData, ORIGINAL, bucket);
      Files.createDirectories(dir);
      var dest = dir.resolve(String.format("%s.jpg", newImage));
      Files.copy(is, dest, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to save image: " + image, e);
    }
    return newImage;
  }
  
  public void delete(@NotNull UUID image) {
    var hex = image.toString();
    var bucket = hex.substring(hex.length() - 2);
    try {
      var dir = Path.of(imageData, ORIGINAL, bucket);
      var file = dir.resolve(String.format("%s.jpg", image));
      Files.deleteIfExists(file);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to delete image: " + image, e);
    }    
  }
}
