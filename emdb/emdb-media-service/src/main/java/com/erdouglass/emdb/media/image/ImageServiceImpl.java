package com.erdouglass.emdb.media.image;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.media.ImageService;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;

/// Default [ImageService] implementation backed by the TMDB image CDN and
/// the local filesystem.
///
/// Saved images are written under `{emdb.image.data}/original/{bucket}/`,
/// where `bucket` is the last two hex characters of the image's [UUID].
/// This sharding keeps any single directory from accumulating an unbounded
/// number of files, which keeps directory listings and filesystem lookups
/// performant as the library grows.
///
/// [UUID]s are generated with the time-based epoch (v7) generator so that
/// IDs created close in time sort close together, improving locality for
/// downstream tooling.
@ApplicationScoped
class ImageServiceImpl implements ImageService {
  private static final String ORIGINAL = "original";
  
  private final NoArgGenerator generator = Generators.timeBasedEpochGenerator();
  
  @Inject
  @RestClient
  ImageClient client;
  
  @Inject
  @ConfigProperty(name = "emdb.image.data")
  String imageData;
  
  @Override
  public UUID save(final String image) {
    var newImage = generator.generate();
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
  
  @Override
  public void delete(final UUID image) {
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
