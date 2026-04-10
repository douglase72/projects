package com.erdouglass.emdb.common;

import java.util.UUID;

import jakarta.validation.constraints.Size;

public record Image(
    UUID name, 
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String tmdbName) {
  
  public static Image of(UUID name, String tmdbName) {
    return new Image(name, tmdbName);
  }  
}
