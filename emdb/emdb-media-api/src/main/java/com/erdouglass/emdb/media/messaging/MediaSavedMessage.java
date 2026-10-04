package com.erdouglass.emdb.media.messaging;

import java.util.Objects;

import com.erdouglass.emdb.shared.kernel.MediaType;
import com.erdouglass.emdb.shared.kernel.PublicId;
import com.erdouglass.emdb.shared.kernel.TmdbId;

public record MediaSavedMessage(
    PublicId id,
    TmdbId tmdbId,
    MediaType mediaType) {

  public MediaSavedMessage {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(tmdbId, "tmdbId is required");
    Objects.requireNonNull(mediaType, "mediaType is required");
  }
  
  public static MediaSavedMessage of(PublicId id, TmdbId tmdbId, MediaType mediaType) {
    return new MediaSavedMessage(id, tmdbId, mediaType);
  }
}
