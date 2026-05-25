package com.erdouglass.emdb.media.domain;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.api.Image;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;

@ApplicationScoped
public class ImageService {
  private static final Logger LOGGER = Logger.getLogger(ImageService.class);
  private static final String ORIGINAL = "original";
  
  private final NoArgGenerator generator = Generators.timeBasedEpochGenerator();
  
  @Inject
  @ConfigProperty(name = "emdb.image.data")
  String imageData;
  
  public Image save(@NotNull Image image) {
    var fileName = generator.generate();
    var hex = fileName.toString();
    var bucket = hex.substring(hex.length() - 2);
    try {
      var dir = Path.of(imageData, ORIGINAL, bucket);
      Files.createDirectories(dir);
      var dest = dir.resolve(String.format("%s.jpg", fileName));
      Files.copy(new ByteArrayInputStream(image.data()), dest, StandardCopyOption.REPLACE_EXISTING);
      LOGGER.debugf("Saved: %s", dest.getFileName());
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to save image: " + image.tmdbName(), e);
    }
    return Image.builder().tmdbName(image.tmdbName()).emdbName(fileName).build();
  }
  
  public ImageUpdate update(String existingTmdbImage, UUID existingEmdbImage, Image image) {
    if (Objects.equals(existingTmdbImage, image.tmdbName())) {
      var current = Image.builder()
          .tmdbName(existingTmdbImage)
          .emdbName(existingEmdbImage)
          .build();
      return new ImageUpdate(current, Optional.empty());
    }
    var old = existingEmdbImage == null
        ? Optional.<Image>empty()
        : Optional.of(Image.builder()
            .tmdbName(existingTmdbImage)
            .emdbName(existingEmdbImage)
            .build());    
    return new ImageUpdate(save(image), old);
  }
  
  public void delete(@NotNull Image image) {
    var hex = image.emdbName().toString();
    var bucket = hex.substring(hex.length() - 2);
    try {
      var dir = Path.of(imageData, ORIGINAL, bucket);
      var file = dir.resolve(String.format("%s.jpg", image.emdbName()));
      Files.deleteIfExists(file);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to delete image: " + image, e);
    }    
  }
}
