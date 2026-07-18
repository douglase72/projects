package com.erdouglass.emdb.media.domain.movie;

import java.text.Normalizer;

import com.erdouglass.emdb.media.MediaConstants;

public record Title(String value) {
  
  public Title {
    if (value == null) {
      throw new IllegalArgumentException("title must not be null");
    }
    value = Normalizer.normalize(value.strip(), Normalizer.Form.NFC);
    if (value.isBlank()) {
      throw new IllegalArgumentException("title must not be blank");
    }
    if (value.length() > MediaConstants.TITLE_MAX_LENGTH) {
      throw new IllegalArgumentException(
          "title must not exceed %d characters".formatted(MediaConstants.TITLE_MAX_LENGTH));
    }
  }
  
  @Override
  public String toString() {
    return value.toString();
  }
}
