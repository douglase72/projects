package com.erdouglass.emdb.media.application.port.inbound.movie;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

import com.erdouglass.emdb.media.UpsertMovieCommand;

public record UpdateMovieCommand(
    String publicId,
    Long version,
    String title,
    Optional<String> releaseDate,
    Optional<BigDecimal> score,
    Optional<String> originalLanguage,
    Optional<String> overview) implements UpsertMovieCommand {

  public UpdateMovieCommand {
    Objects.requireNonNull(publicId, "publicId is required");
    Objects.requireNonNull(version, "version is required");    
    Objects.requireNonNull(title, "title is reqired");
  }
}
