package com.erdouglass.emdb.media.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.image.Image;

@ApplicationScoped
public class ImageService {
  private static final Logger LOGGER = Logger.getLogger(ImageService.class);

  @Inject
  @ConfigProperty(name = "emdb.image.data")
  String imageData;
  
  public void save(Image image) {
    if (image == null) {
      return;
    }
    var hex = image.name().toString();
    var bucket = hex.substring(hex.length() - 2);
    try {
      var dir = Path.of(imageData, bucket);
      Files.createDirectories(dir);
      var dest = dir.resolve(String.format("%s.jpg", image));
      Files.copy(new ByteArrayInputStream(image.data()), dest, StandardCopyOption.REPLACE_EXISTING);
      LOGGER.debugf("Saved: %s", dest.toAbsolutePath());
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to save image: " + image, e);
    }
  }
  
  public ImageUpdate update(UUID source, Image target) {
    if (source.equals(target.name())) {
      return new ImageUpdate(target, Optional.empty());
    }
    save(target);
    return new ImageUpdate(target, Optional.of(source));
  }
  
  public void delete(UUID image) {
    var hex = image.toString();
    var bucket = hex.substring(hex.length() - 2);
    try {
      var dir = Path.of(imageData, bucket);
      var file = dir.resolve(String.format("%s.jpg", image));
      Files.deleteIfExists(file);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to delete image: " + image, e);
    }    
  }
  
  public record ImageUpdate(Image image, Optional<UUID> toDelete) {}
}
