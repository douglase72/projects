package com.erdouglass.emdb.media;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/// Manages the lifecycle of media images on the local image store.
///
/// Implementations are responsible for fetching images from an external
/// source (e.g., the TMDB CDN) and writing them to the configured local
/// data directory, as well as deleting them when they are no longer
/// referenced by any media entity.
///
/// All callers should inject this interface rather than any concrete
/// implementation; Jakarta Bean Validation constraints declared here are
/// enforced on the proxied method call.
public interface ImageService {
  
  /// Fetches the image identified by the given external name and stores a
  /// copy locally under a newly-generated [UUID].
  ///
  /// @param image the external image name (e.g., a TMDB image path); must be non-blank
  /// @return the [UUID] assigned to the locally-stored copy  
  UUID save(@NotBlank String image);

  /// Deletes the locally-stored image with the given [UUID], if it exists.
  /// No-op if no file is found.
  ///
  /// @param image the [UUID] of the image to delete; must be non-null  
  void delete(@NotNull UUID image);
}
